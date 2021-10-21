package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static io.vlingo.xoom.auth.test.Assertions.assertEventDispatched;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleEntityTest {

  private final TenantId TENANT_ID = TenantId.from("f3e1f662-d7b4-4f9e-aa6c-6d16d4c70437");
  private final String ROLE_NAME = "role-a";
  private final String ROLE_DESCRIPTION = "Role A";
  private final RoleId ROLE_ID = RoleId.from(TENANT_ID, ROLE_NAME);

  private World world;
  private MockDispatcher dispatcher;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp() {
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    final EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);
    entryAdapterProvider.registerAdapter(RoleProvisioned.class, new RoleProvisionedAdapter());
    entryAdapterProvider.registerAdapter(RoleDescriptionChanged.class, new RoleDescriptionChangedAdapter());
    entryAdapterProvider.registerAdapter(GroupAssignedToRole.class, new GroupAssignedToRoleAdapter());
    entryAdapterProvider.registerAdapter(GroupUnassignedFromRole.class, new GroupUnassignedFromRoleAdapter());
    entryAdapterProvider.registerAdapter(UserAssignedToRole.class, new UserAssignedToRoleAdapter());
    entryAdapterProvider.registerAdapter(UserUnassignedFromRole.class, new UserUnassignedFromRoleAdapter());
    entryAdapterProvider.registerAdapter(RolePermissionAttached.class, new RolePermissionAttachedAdapter());
    entryAdapterProvider.registerAdapter(RolePermissionDetached.class, new RolePermissionDetachedAdapter());

    final Journal<String> journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, RoleEntity.class, RoleEntity.class.getSimpleName()));
  }

  @AfterEach
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void roleIsProvisionedForTheTenant() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Completes<RoleState> outcome = roleOf(ROLE_ID).provisionRole(ROLE_NAME, ROLE_DESCRIPTION);

    assertCompletes(outcome, state -> {
      assertEquals(ROLE_ID, state.roleId);
      assertEquals(ROLE_NAME, state.name);
      assertEquals(ROLE_DESCRIPTION, state.description);
      assertEventDispatched(dispatcherAccess, 1, RoleProvisioned.class);
    });
  }

  @Test
  public void roleDescriptionIsChanged() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<RoleState> outcome = givenRoleExists(ROLE_ID)
            .andThenTo(r -> roleOf(ROLE_ID).changeDescription("updated-role-description"));

    assertCompletes(outcome, state -> {
      assertEquals(ROLE_ID, state.roleId);
      assertEquals(ROLE_NAME, state.name);
      assertEquals("updated-role-description", state.description);
      assertEventDispatched(dispatcherAccess, 2, RoleDescriptionChanged.class);
    });
  }

  @Test
  public void groupIsAssignedToRole() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<RoleState> outcome = givenRoleExists(ROLE_ID)
            .andThenTo(r -> roleOf(ROLE_ID).assignGroup("updated-role-name"));

    assertCompletes(outcome, state -> {
      assertEquals(ROLE_ID, state.roleId);
      assertEquals("updated-role-name", state.name);
      assertEquals(ROLE_DESCRIPTION, state.description);
      assertEventDispatched(dispatcherAccess, 2, GroupAssignedToRole.class);
    });
  }

  @Test
  public void groupIsUnassignedFromRole() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<RoleState> outcome = givenRoleExists(ROLE_ID)
            .andThenTo(r -> roleOf(ROLE_ID).unassignGroup("updated-role-name"));

    assertCompletes(outcome, state -> {
      assertEquals(ROLE_ID, state.roleId);
      assertEquals("updated-role-name", state.name);
      assertEquals(ROLE_DESCRIPTION, state.description);
      assertEventDispatched(dispatcherAccess, 2, GroupUnassignedFromRole.class);
    });
  }

  @Test
  public void userIsAssignedToRole() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<RoleState> outcome = givenRoleExists(ROLE_ID)
            .andThenTo(r -> roleOf(ROLE_ID).assignUser("updated-role-name"));

    assertCompletes(outcome, state -> {
      assertEquals(ROLE_ID, state.roleId);
      assertEquals("updated-role-name", state.name);
      assertEquals(ROLE_DESCRIPTION, state.description);
      assertEventDispatched(dispatcherAccess, 2, UserAssignedToRole.class);
    });
  }

  @Test
  public void userIsUnassignedFromRole() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<RoleState> outcome = givenRoleExists(ROLE_ID)
            .andThenTo(r -> roleOf(ROLE_ID).unassignUser("updated-role-name"));

    assertCompletes(outcome, state -> {
      assertEquals(ROLE_ID, state.roleId);
      assertEquals("updated-role-name", state.name);
      assertEquals(ROLE_DESCRIPTION, state.description);
      assertEventDispatched(dispatcherAccess, 2, UserUnassignedFromRole.class);
    });
  }

  @Test
  public void permissionIsAttachedToRole() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<RoleState> outcome = givenRoleExists(ROLE_ID)
            .andThenTo(r -> roleOf(ROLE_ID).attach("updated-role-name"));

    assertCompletes(outcome, state -> {
      assertEquals(ROLE_ID, state.roleId);
      assertEquals("updated-role-name", state.name);
      assertEquals(ROLE_DESCRIPTION, state.description);
      assertEventDispatched(dispatcherAccess, 2, RolePermissionAttached.class);
    });
  }

  @Test
  public void permissionIsDetachedFromRole() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<RoleState> outcome = givenRoleExists(ROLE_ID)
            .andThenTo(state -> roleOf(ROLE_ID).detach("updated-role-name"));

    assertCompletes(outcome, state -> {
      assertEquals(ROLE_ID, state.roleId);
      assertEquals("updated-role-name", state.name);
      assertEquals(ROLE_DESCRIPTION, state.description);
      assertEventDispatched(dispatcherAccess, 2, RolePermissionDetached.class);
    });
  }

  private Completes<RoleState> givenRoleExists(RoleId roleId) {
    return roleOf(roleId).provisionRole(ROLE_NAME, ROLE_DESCRIPTION);
  }

  private Role roleOf(RoleId roleId) {
    return world.actorFor(Role.class, RoleEntity.class, roleId);
  }
}
