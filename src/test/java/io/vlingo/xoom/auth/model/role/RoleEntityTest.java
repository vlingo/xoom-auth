package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.vlingo.xoom.auth.test.Assertions.assertEventDispatched;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleEntityTest {

  private final TenantId TENANT_ID = TenantId.from("f3e1f662-d7b4-4f9e-aa6c-6d16d4c70437");
  private final String ROLE_NAME = "role-a";
  private final String ROLE_DESCRIPTION = "Role A";
  private final RoleId ROLE_ID = RoleId.from(TENANT_ID, ROLE_NAME);

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp(){
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);

    entryAdapterProvider.registerAdapter(RoleProvisioned.class, new RoleProvisionedAdapter());
    entryAdapterProvider.registerAdapter(RoleDescriptionChanged.class, new RoleDescriptionChangedAdapter());
    entryAdapterProvider.registerAdapter(GroupAssignedToRole.class, new GroupAssignedToRoleAdapter());
    entryAdapterProvider.registerAdapter(GroupUnassignedFromRole.class, new GroupUnassignedFromRoleAdapter());
    entryAdapterProvider.registerAdapter(UserAssignedToRole.class, new UserAssignedToRoleAdapter());
    entryAdapterProvider.registerAdapter(UserUnassignedFromRole.class, new UserUnassignedFromRoleAdapter());
    entryAdapterProvider.registerAdapter(RolePermissionAttached.class, new RolePermissionAttachedAdapter());
    entryAdapterProvider.registerAdapter(RolePermissionDetached.class, new RolePermissionDetachedAdapter());

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, RoleEntity.class, RoleEntity.class.getSimpleName()));
  }

  @Test
  public void provisionRole() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = roleOf(ROLE_ID).provisionRole(ROLE_NAME, ROLE_DESCRIPTION).await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals(ROLE_NAME, state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEventDispatched(dispatcherAccess, 1, RoleProvisioned.class);
  }

  @Test
  public void changeDescription() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenRoleExists(ROLE_ID);

    final RoleState state = roleOf(ROLE_ID).changeDescription("updated-role-description").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals(ROLE_NAME, state.name);
    assertEquals("updated-role-description", state.description);
    assertEventDispatched(dispatcherAccess, 2, RoleDescriptionChanged.class);
  }

  @Test
  public void assignGroup() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenRoleExists(ROLE_ID);

    final RoleState state = roleOf(ROLE_ID).assignGroup("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEventDispatched(dispatcherAccess, 2, GroupAssignedToRole.class);
  }

  @Test
  public void unassignGroup() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenRoleExists(ROLE_ID);

    final RoleState state = roleOf(ROLE_ID).unassignGroup("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEventDispatched(dispatcherAccess, 2, GroupUnassignedFromRole.class);
  }

  @Test
  public void assignUser() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenRoleExists(ROLE_ID);

    final RoleState state = roleOf(ROLE_ID).assignUser("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEventDispatched(dispatcherAccess, 2, UserAssignedToRole.class);
  }

  @Test
  public void unassignUser() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenRoleExists(ROLE_ID);

    final RoleState state = roleOf(ROLE_ID).unassignUser("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEventDispatched(dispatcherAccess, 2, UserUnassignedFromRole.class);
  }

  @Test
  public void attach() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenRoleExists(ROLE_ID);

    final RoleState state = roleOf(ROLE_ID).attach("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEventDispatched(dispatcherAccess, 2, RolePermissionAttached.class);
  }

  @Test
  public void detach() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenRoleExists(ROLE_ID);

    final RoleState state = roleOf(ROLE_ID).detach("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEventDispatched(dispatcherAccess, 2, RolePermissionDetached.class);
  }

  private void givenRoleExists(RoleId roleId) {
    roleOf(roleId).provisionRole(ROLE_NAME, ROLE_DESCRIPTION).await();
  }

  private Role roleOf(RoleId roleId) {
    return world.actorFor(Role.class, RoleEntity.class, roleId);
  }
}
