package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.RoleDescriptionChangedAdapter;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.auth.infrastructure.persistence.GroupUnassignedFromRoleAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.UserUnassignedFromRoleAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.UserAssignedToRoleAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.RoleProvisionedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.RolePermissionAttachedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.RolePermissionDetachedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.MockDispatcher;
import io.vlingo.xoom.auth.infrastructure.persistence.GroupAssignedToRoleAdapter;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RoleEntityTest {

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;
  private Role role;

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

    role = world.actorFor(Role.class, RoleEntity.class, "#1");
  }

  private static final String TENANT_ID_FOR_PROVISION_ROLE_TEST = "role-tenantId";
  private static final String NAME_FOR_PROVISION_ROLE_TEST = "role-name";
  private static final String DESCRIPTION_FOR_PROVISION_ROLE_TEST = "role-description";

  @Test
  public void provisionRole() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = role.provisionRole(TENANT_ID_FOR_PROVISION_ROLE_TEST, NAME_FOR_PROVISION_ROLE_TEST, DESCRIPTION_FOR_PROVISION_ROLE_TEST).await();

    assertEquals(state.tenantId, "role-tenantId");
    assertEquals(state.name, "role-name");
    assertEquals(state.description, "role-description");
    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(RoleProvisioned.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }

  private static final String TENANT_ID_FOR_CHANGE_DESCRIPTION_TEST = "updated-role-tenantId";
  private static final String NAME_FOR_CHANGE_DESCRIPTION_TEST = "updated-role-name";
  private static final String DESCRIPTION_FOR_CHANGE_DESCRIPTION_TEST = "updated-role-description";

  @Test
  public void changeDescription() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = role.changeDescription(TENANT_ID_FOR_CHANGE_DESCRIPTION_TEST, NAME_FOR_CHANGE_DESCRIPTION_TEST, DESCRIPTION_FOR_CHANGE_DESCRIPTION_TEST).await();

    assertEquals(state.tenantId, "updated-role-tenantId");
    assertEquals(state.name, "updated-role-name");
    assertEquals(state.description, "updated-role-description");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(RoleDescriptionChanged.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_ASSIGN_GROUP_TEST = "updated-role-tenantId";
  private static final String NAME_FOR_ASSIGN_GROUP_TEST = "updated-role-name";

  @Test
  public void assignGroup() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = role.assignGroup(TENANT_ID_FOR_ASSIGN_GROUP_TEST, NAME_FOR_ASSIGN_GROUP_TEST).await();

    assertEquals(state.description, "role-description");
    assertEquals(state.tenantId, "updated-role-tenantId");
    assertEquals(state.name, "updated-role-name");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupAssignedToRole.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_UNASSIGN_GROUP_TEST = "updated-role-tenantId";
  private static final String NAME_FOR_UNASSIGN_GROUP_TEST = "updated-role-name";

  @Test
  public void unassignGroup() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = role.unassignGroup(TENANT_ID_FOR_UNASSIGN_GROUP_TEST, NAME_FOR_UNASSIGN_GROUP_TEST).await();

    assertEquals(state.description, "role-description");
    assertEquals(state.tenantId, "updated-role-tenantId");
    assertEquals(state.name, "updated-role-name");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupUnassignedFromRole.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_ASSIGN_USER_TEST = "updated-role-tenantId";
  private static final String NAME_FOR_ASSIGN_USER_TEST = "updated-role-name";

  @Test
  public void assignUser() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = role.assignUser(TENANT_ID_FOR_ASSIGN_USER_TEST, NAME_FOR_ASSIGN_USER_TEST).await();

    assertEquals(state.description, "role-description");
    assertEquals(state.tenantId, "updated-role-tenantId");
    assertEquals(state.name, "updated-role-name");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserAssignedToRole.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_UNASSIGN_USER_TEST = "updated-role-tenantId";
  private static final String NAME_FOR_UNASSIGN_USER_TEST = "updated-role-name";

  @Test
  public void unassignUser() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = role.unassignUser(TENANT_ID_FOR_UNASSIGN_USER_TEST, NAME_FOR_UNASSIGN_USER_TEST).await();

    assertEquals(state.description, "role-description");
    assertEquals(state.tenantId, "updated-role-tenantId");
    assertEquals(state.name, "updated-role-name");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserUnassignedFromRole.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_ATTACH_TEST = "updated-role-tenantId";
  private static final String NAME_FOR_ATTACH_TEST = "updated-role-name";

  @Test
  public void attach() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = role.attach(TENANT_ID_FOR_ATTACH_TEST, NAME_FOR_ATTACH_TEST).await();

    assertEquals(state.description, "role-description");
    assertEquals(state.tenantId, "updated-role-tenantId");
    assertEquals(state.name, "updated-role-name");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(RolePermissionAttached.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_DETACH_TEST = "updated-role-tenantId";
  private static final String NAME_FOR_DETACH_TEST = "updated-role-name";

  @Test
  public void detach() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final RoleState state = role.detach(TENANT_ID_FOR_DETACH_TEST, NAME_FOR_DETACH_TEST).await();

    assertEquals(state.description, "role-description");
    assertEquals(state.tenantId, "updated-role-tenantId");
    assertEquals(state.name, "updated-role-name");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(RolePermissionDetached.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_ENTITY_CREATION = "role-tenantId";
  private static final String NAME_FOR_ENTITY_CREATION = "role-name";
  private static final String DESCRIPTION_FOR_ENTITY_CREATION = "role-description";

  private void _createEntity() {
    role.provisionRole(TENANT_ID_FOR_ENTITY_CREATION, NAME_FOR_ENTITY_CREATION, DESCRIPTION_FOR_ENTITY_CREATION).await();
  }
}
