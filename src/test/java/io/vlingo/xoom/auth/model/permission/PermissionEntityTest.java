package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.value.Constraint;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static io.vlingo.xoom.auth.test.Assertions.assertEventDispatched;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PermissionEntityTest {

  private final TenantId TENANT_ID = TenantId.from("d8e6476f-00c2-4bfd-9790-bf6388ed78d2");
  private final String PERMISSION_NAME = "permission-a";
  private final String PERMISSION_DESCRIPTION = "Permission A description";
  private final PermissionId PERMISSION_ID = PermissionId.from(TENANT_ID, PERMISSION_NAME);

  private World world;
  private MockDispatcher dispatcher;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp() {
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);

    entryAdapterProvider.registerAdapter(PermissionProvisioned.class, new PermissionProvisionedAdapter());
    entryAdapterProvider.registerAdapter(PermissionConstraintEnforced.class, new PermissionConstraintEnforcedAdapter());
    entryAdapterProvider.registerAdapter(PermissionConstraintReplacementEnforced.class, new PermissionConstraintReplacementEnforcedAdapter());
    entryAdapterProvider.registerAdapter(PermissionConstraintForgotten.class, new PermissionConstraintForgottenAdapter());
    entryAdapterProvider.registerAdapter(PermissionDescriptionChanged.class, new PermissionDescriptionChangedAdapter());

    final Journal<String> journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, PermissionEntity.class, PermissionEntity.class.getSimpleName()));
  }

  @Test
  public void permissionIsProvisionedForTheTenant() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Completes<PermissionState> outcome = permissionOf(PERMISSION_ID)
            .provisionPermission(PERMISSION_NAME, PERMISSION_DESCRIPTION);

    assertCompletes(outcome, state -> {
      assertEquals(PERMISSION_NAME, state.name);
      assertEquals(PERMISSION_DESCRIPTION, state.description);
      assertEquals(PERMISSION_ID, state.id);
      assertEventDispatched(dispatcherAccess, 1, PermissionProvisioned.class);
    });
  }

  @Test
  public void permissionConstraintIsEnforced() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Constraint constraint = Constraint.from(Constraint.Type.String, "constraint-name", "A", "constraint-description");

    final Completes<PermissionState> outcome = givenPermissionIsProvisioned(PERMISSION_ID)
            .andThenTo(p -> permissionOf(PERMISSION_ID).enforce(constraint));

    assertCompletes(outcome, state -> {
      assertEquals(PERMISSION_ID, state.id);
      assertContainsConstraint(constraint, state);
      assertEventDispatched(dispatcherAccess, 2, PermissionConstraintEnforced.class);
    });
  }

  @Test
  public void permissionConstraintReplacementIsEnforced() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(3);
    final Constraint constraintA = Constraint.from(Constraint.Type.String, "constraint-A", "A", "constraint-description");
    final Constraint replacementConstraint = Constraint.from(Constraint.Type.String, "constraint-B", "updated-permission-constraints-value", "updated-permission-constraints-description");

    final Completes<PermissionState> outcome = givenPermissionIsProvisioned(PERMISSION_ID)
            .andThenTo(p -> permissionOf(PERMISSION_ID).enforce(constraintA))
            .andThenTo(p -> permissionOf(PERMISSION_ID).enforceReplacement("constraint-A", replacementConstraint));

    assertCompletes(outcome, state -> {
      assertEquals(1, state.constraints.size());
      assertNotContainsConstraint(constraintA, state);
      assertContainsConstraint(replacementConstraint, state);
      assertEventDispatched(dispatcherAccess, 3, PermissionConstraintReplacementEnforced.class);
    });
  }

  @Test
  public void permissionConstraintIsForgotten() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Constraint constraintA = Constraint.from(Constraint.Type.String, "constraint-A", "A", "constraint-description");
    final Constraint constraintB = Constraint.from(Constraint.Type.String, "constraint-B", "B", "constraint-description");

    final Completes<PermissionState> outcome = givenPermissionIsProvisioned(PERMISSION_ID)
            .andThenTo(p -> permissionOf(PERMISSION_ID).enforce(constraintA))
            .andThenTo(p -> permissionOf(PERMISSION_ID).enforce(constraintB))
            .andThenTo(p -> permissionOf(PERMISSION_ID).forget("constraint-A"));

    assertCompletes(outcome, state -> {
      assertEquals(1, state.constraints.size());
      assertNotContainsConstraint(constraintA, state);
      assertContainsConstraint(constraintB, state);
      assertEventDispatched(dispatcherAccess, 4, PermissionConstraintForgotten.class);
    });
  }

  @Test
  public void changeDescription() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<PermissionState> outcome = givenPermissionIsProvisioned(PERMISSION_ID)
            .andThenTo(p -> permissionOf(PERMISSION_ID).changeDescription("updated-permission-description"));

    assertCompletes(outcome, state -> {
      assertEquals(PERMISSION_NAME, state.name);
      assertEquals("updated-permission-description", state.description);
      assertEquals(PERMISSION_ID, state.id);
      assertEventDispatched(dispatcherAccess, 2, PermissionDescriptionChanged.class);
    });
  }

  private Completes<PermissionState> givenPermissionIsProvisioned(final PermissionId permissionId) {
    return permissionOf(permissionId).provisionPermission(PERMISSION_NAME, PERMISSION_DESCRIPTION);
  }

  private Permission permissionOf(final PermissionId permissionId) {
    return world.actorFor(Permission.class, PermissionEntity.class, permissionId);
  }

  private void assertContainsConstraint(final Constraint constraint, final PermissionState state) {
    assertEquals(new HashSet<>(Collections.singletonList(constraint)), state.constraints);
  }

  private void assertNotContainsConstraint(final Constraint constraint, final PermissionState state) {
    state.constraints.forEach(c -> assertNotEquals(constraint, c));
  }
}
