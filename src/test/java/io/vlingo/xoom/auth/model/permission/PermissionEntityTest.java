package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.value.Constraint;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static io.vlingo.xoom.auth.test.Assertions.assertEventDispatched;
import static org.junit.jupiter.api.Assertions.*;

public class PermissionEntityTest {

  private final TenantId TENANT_ID = TenantId.from("d8e6476f-00c2-4bfd-9790-bf6388ed78d2");
  private final String PERMISSION_NAME = "permission-a";
  private final String PERMISSION_DESCRIPTION = "Permission A description";
  private final PermissionId PERMISSION_ID = PermissionId.from(TENANT_ID, PERMISSION_NAME);

  private World world;
  private Journal<String> journal;
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

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, PermissionEntity.class, PermissionEntity.class.getSimpleName()));
  }

  @Test
  public void permissionIsProvisionedForTheTenant() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final PermissionState state = permissionOf(PERMISSION_ID).provisionPermission(PERMISSION_NAME, PERMISSION_DESCRIPTION).await();

    assertEquals(PERMISSION_NAME, state.name);
    assertEquals(PERMISSION_DESCRIPTION, state.description);
    assertEquals(PERMISSION_ID, state.id);
    assertEventDispatched(dispatcherAccess, 1, PermissionProvisioned.class);
  }

  @Test
  public void permissionConstraintIsEnforced() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenPermissionIsProvisioned(PERMISSION_ID);

    final Constraint constraint = Constraint.from(Constraint.Type.String, "constraint-name", "A", "constraint-description");
    final PermissionState state = permissionOf(PERMISSION_ID).enforce(constraint).await();

    assertEquals(PERMISSION_ID, state.id);
    assertEquals(new HashSet<>(Arrays.asList(constraint)), state.constraints);
    assertEventDispatched(dispatcherAccess, 2, PermissionConstraintEnforced.class);
  }

  @Test
  public void permissionConstraintReplacementIsEnforced() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(3);

    givenPermissionIsProvisionedWithEnforcedConstraint(PERMISSION_ID, "constraint-A");

    final Constraint replacementConstraint = Constraint.from(Constraint.Type.String, "constraint-B", "updated-permission-constraints-value", "updated-permission-constraints-description");
    final PermissionState state = permissionOf(PERMISSION_ID).enforceReplacement("constraint-A", replacementConstraint).await();

    assertEquals(1, state.constraints.size());
    assertEquals(replacementConstraint, state.constraints.stream().findFirst().get());
    assertEventDispatched(dispatcherAccess, 3, PermissionConstraintReplacementEnforced.class);
  }

  @Test
  public void forget() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenPermissionIsProvisioned(PERMISSION_ID);

    final Constraint constraint = Constraint.from(Constraint.Type.String, "updated-permission-constraints-name", "updated-permission-constraints-value", "updated-permission-constraints-description");
    final PermissionState state = permissionOf(PERMISSION_ID).forget(constraint).await();

    assertEquals(PERMISSION_NAME, state.name);
    assertEquals(PERMISSION_DESCRIPTION, state.description);
    assertEquals(PERMISSION_ID, state.id);
    assertNotNull(state.constraints);
    assertEventDispatched(dispatcherAccess, 2, PermissionConstraintForgotten.class);
  }

  @Test
  public void changeDescription() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenPermissionIsProvisioned(PERMISSION_ID);

    final PermissionState state = permissionOf(PERMISSION_ID).changeDescription("updated-permission-description").await();

    assertEquals(PERMISSION_NAME, state.name);
    assertEquals("updated-permission-description", state.description);
    assertEquals(PERMISSION_ID, state.id);
    assertEventDispatched(dispatcherAccess, 2, PermissionDescriptionChanged.class);
  }

  private void givenPermissionIsProvisioned(final PermissionId permissionId) {
    permissionOf(permissionId).provisionPermission(PERMISSION_NAME, PERMISSION_DESCRIPTION).await();
  }

  private void givenPermissionIsProvisionedWithEnforcedConstraint(final PermissionId permissionId, final String name) {
    givenPermissionIsProvisioned(permissionId);
    permissionOf(PERMISSION_ID).enforce(
            Constraint.from(
                    Constraint.Type.String,
                    name,
                    "A",
                    "constraint-description"
            )
    ).await(100);
  }

  private Permission permissionOf(PermissionId permissionId) {
    return world.actorFor(Permission.class, PermissionEntity.class, permissionId);
  }
}
