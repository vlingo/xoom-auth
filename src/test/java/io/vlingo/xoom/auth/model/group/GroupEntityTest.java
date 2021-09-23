package io.vlingo.xoom.auth.model.group;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.auth.infrastructure.persistence.UserUnassignedFromGroupAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.GroupProvisionedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.GroupUnassignedFromGroupAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.UserAssignedToGroupAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.MockDispatcher;
import io.vlingo.xoom.auth.infrastructure.persistence.GroupDescriptionChangedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.GroupAssignedToGroupAdapter;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GroupEntityTest {

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;
  private Group group;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp(){
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);

    entryAdapterProvider.registerAdapter(GroupProvisioned.class, new GroupProvisionedAdapter());
    entryAdapterProvider.registerAdapter(GroupDescriptionChanged.class, new GroupDescriptionChangedAdapter());
    entryAdapterProvider.registerAdapter(GroupAssignedToGroup.class, new GroupAssignedToGroupAdapter());
    entryAdapterProvider.registerAdapter(GroupUnassignedFromGroup.class, new GroupUnassignedFromGroupAdapter());
    entryAdapterProvider.registerAdapter(UserAssignedToGroup.class, new UserAssignedToGroupAdapter());
    entryAdapterProvider.registerAdapter(UserUnassignedFromGroup.class, new UserUnassignedFromGroupAdapter());

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, GroupEntity.class, GroupEntity.class.getSimpleName()));

    group = world.actorFor(Group.class, GroupEntity.class, "#1");
  }

  private static final String NAME_FOR_PROVISION_GROUP_TEST = "group-name";
  private static final String DESCRIPTION_FOR_PROVISION_GROUP_TEST = "group-description";
  private static final String TENANT_ID_FOR_PROVISION_GROUP_TEST = "group-tenantId";

  @Test
  public void provisionGroup() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = group.provisionGroup(NAME_FOR_PROVISION_GROUP_TEST, DESCRIPTION_FOR_PROVISION_GROUP_TEST, TENANT_ID_FOR_PROVISION_GROUP_TEST).await();

    assertEquals(state.name, "group-name");
    assertEquals(state.description, "group-description");
    assertEquals(state.tenantId, "group-tenantId");
    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupProvisioned.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }

  private static final String DESCRIPTION_FOR_CHANGE_DESCRIPTION_TEST = "updated-group-description";
  private static final String TENANT_ID_FOR_CHANGE_DESCRIPTION_TEST = "updated-group-tenantId";

  @Test
  public void changeDescription() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = group.changeDescription(DESCRIPTION_FOR_CHANGE_DESCRIPTION_TEST, TENANT_ID_FOR_CHANGE_DESCRIPTION_TEST).await();

    assertEquals(state.name, "group-name");
    assertEquals(state.description, "updated-group-description");
    assertEquals(state.tenantId, "updated-group-tenantId");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupDescriptionChanged.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_ASSIGN_GROUP_TEST = "updated-group-tenantId";

  @Test
  public void assignGroup() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = group.assignGroup(TENANT_ID_FOR_ASSIGN_GROUP_TEST).await();

    assertEquals(state.name, "group-name");
    assertEquals(state.description, "group-description");
    assertEquals(state.tenantId, "updated-group-tenantId");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupAssignedToGroup.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_UNASSIGN_GROUP_TEST = "updated-group-tenantId";

  @Test
  public void unassignGroup() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = group.unassignGroup(TENANT_ID_FOR_UNASSIGN_GROUP_TEST).await();

    assertEquals(state.name, "group-name");
    assertEquals(state.description, "group-description");
    assertEquals(state.tenantId, "updated-group-tenantId");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupUnassignedFromGroup.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_ASSIGN_USER_TEST = "updated-group-tenantId";

  @Test
  public void assignUser() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = group.assignUser(TENANT_ID_FOR_ASSIGN_USER_TEST).await();

    assertEquals(state.name, "group-name");
    assertEquals(state.description, "group-description");
    assertEquals(state.tenantId, "updated-group-tenantId");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserAssignedToGroup.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_UNASSIGN_USER_TEST = "updated-group-tenantId";

  @Test
  public void unassignUser() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = group.unassignUser(TENANT_ID_FOR_UNASSIGN_USER_TEST).await();

    assertEquals(state.name, "group-name");
    assertEquals(state.description, "group-description");
    assertEquals(state.tenantId, "updated-group-tenantId");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserUnassignedFromGroup.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String NAME_FOR_ENTITY_CREATION = "group-name";
  private static final String DESCRIPTION_FOR_ENTITY_CREATION = "group-description";
  private static final String TENANT_ID_FOR_ENTITY_CREATION = "group-tenantId";

  private void _createEntity() {
    group.provisionGroup(NAME_FOR_ENTITY_CREATION, DESCRIPTION_FOR_ENTITY_CREATION, TENANT_ID_FOR_ENTITY_CREATION).await();
  }
}
