package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionConstraintEnforcedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionProvisionedAdapter;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.symbio.BaseEntry;
import java.util.*;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionConstraintForgottenAdapter;
import io.vlingo.xoom.auth.model.value.*;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionDescriptionChangedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionConstraintReplacementEnforcedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.MockDispatcher;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.*;

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
  public void provisionPermission() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final PermissionState state = permissionOf(PERMISSION_ID).provisionPermission(PERMISSION_NAME, PERMISSION_DESCRIPTION).await();

    assertEquals(PERMISSION_NAME, state.name);
    assertEquals(PERMISSION_DESCRIPTION, state.description);
    assertEquals(PERMISSION_ID, state.id);
    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(PermissionProvisioned.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }

  @Test
  public void enforce() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenPermissionIsProvisioned(PERMISSION_ID);

    final Constraint constraint = Constraint.from("updated-permission-constraints-description", "updated-permission-constraints-name", "updated-permission-constraints-type", "updated-permission-constraints-value");
    final PermissionState state = permissionOf(PERMISSION_ID).enforce(constraint).await();

    assertEquals(PERMISSION_NAME, state.name);
    assertEquals(PERMISSION_DESCRIPTION, state.description);
    assertEquals(PERMISSION_ID, state.id);
    assertNotNull(state.constraints);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(PermissionConstraintEnforced.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  @Test
  public void enforceReplacement() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenPermissionIsProvisioned(PERMISSION_ID);

    final Constraint constraint = Constraint.from("updated-permission-constraints-description", "updated-permission-constraints-name", "updated-permission-constraints-type", "updated-permission-constraints-value");
    final PermissionState state = permissionOf(PERMISSION_ID).enforceReplacement(constraint).await();

    assertEquals(PERMISSION_NAME, state.name);
    assertEquals(PERMISSION_DESCRIPTION, state.description);
    assertEquals(PERMISSION_ID, state.id);
    assertNotNull(state.constraints);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(PermissionConstraintReplacementEnforced.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  @Test
  public void forget() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenPermissionIsProvisioned(PERMISSION_ID);

    final Constraint constraint = Constraint.from("updated-permission-constraints-description", "updated-permission-constraints-name", "updated-permission-constraints-type", "updated-permission-constraints-value");
    final PermissionState state = permissionOf(PERMISSION_ID).forget(constraint).await();

    assertEquals(PERMISSION_NAME, state.name);
    assertEquals(PERMISSION_DESCRIPTION, state.description);
    assertEquals(PERMISSION_ID, state.id);
    assertNotNull(state.constraints);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(PermissionConstraintForgotten.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  @Test
  public void changeDescription() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenPermissionIsProvisioned(PERMISSION_ID);

    final PermissionState state = permissionOf(PERMISSION_ID).changeDescription("updated-permission-description").await();

    assertEquals(PERMISSION_NAME, state.name);
    assertEquals("updated-permission-description", state.description);
    assertEquals(PERMISSION_ID, state.id);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(PermissionDescriptionChanged.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private void givenPermissionIsProvisioned(final PermissionId permissionId) {
    permissionOf(permissionId).provisionPermission(PERMISSION_NAME, PERMISSION_DESCRIPTION).await();
  }

  private Permission permissionOf(PermissionId permissionId) {
    return world.actorFor(Permission.class, PermissionEntity.class, permissionId);
  }
}
