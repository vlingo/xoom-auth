package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleEntityTest {

  private final String TENANT_ID = "f3e1f662-d7b4-4f9e-aa6c-6d16d4c70437";
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
    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(RoleProvisioned.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }

  @Test
  public void changeDescription() {
    givenRoleExists(ROLE_ID);
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = roleOf(ROLE_ID).changeDescription("updated-role-description").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals(ROLE_NAME, state.name);
    assertEquals("updated-role-description", state.description);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(RoleDescriptionChanged.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  @Test
  public void assignGroup() {
    givenRoleExists(ROLE_ID);
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = roleOf(ROLE_ID).assignGroup("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupAssignedToRole.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  @Test
  public void unassignGroup() {
    givenRoleExists(ROLE_ID);
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = roleOf(ROLE_ID).unassignGroup("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupUnassignedFromRole.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  @Test
  public void assignUser() {
    givenRoleExists(ROLE_ID);
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = roleOf(ROLE_ID).assignUser("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserAssignedToRole.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  @Test
  public void unassignUser() {
    givenRoleExists(ROLE_ID);
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = roleOf(ROLE_ID).unassignUser("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserUnassignedFromRole.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  @Test
  public void attach() {
    givenRoleExists(ROLE_ID);
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = roleOf(ROLE_ID).attach("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(RolePermissionAttached.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  @Test
  public void detach() {
    givenRoleExists(ROLE_ID);
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = roleOf(ROLE_ID).detach("updated-role-name").await();

    assertEquals(ROLE_ID, state.roleId);
    assertEquals("updated-role-name", state.name);
    assertEquals(ROLE_DESCRIPTION, state.description);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(RolePermissionDetached.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private void givenRoleExists(RoleId roleId) {
    roleOf(roleId).provisionRole(ROLE_NAME, ROLE_DESCRIPTION).await();
  }

  private Role roleOf(RoleId roleId) {
    return world.actorFor(Role.class, RoleEntity.class, roleId);
  }
}
