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

  private final String TENANT_ID = "7c7161d4-12c9-4dde-9e8f-26c40bfc7902";
  private final String GROUP_NAME = "Group A";
  private final String GROUP_DESCRIPTION = "Group A description";
  private final GroupId GROUP_ID = GroupId.from(TENANT_ID, GROUP_NAME);

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;

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
  }

  @Test
  public void provisionGroup() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = groupOf(GROUP_ID).provisionGroup(GROUP_NAME, GROUP_DESCRIPTION).await();

    assertEquals(GROUP_NAME, state.name);
    assertEquals(GROUP_DESCRIPTION, state.description);
    assertEquals(GROUP_ID, state.id);
    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupProvisioned.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }

  @Test
  public void changeDescription() {
    givenGroupIsProvisioned(GROUP_ID, GROUP_NAME, GROUP_DESCRIPTION);

    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = groupOf(GROUP_ID).changeDescription("updated-groupOf-description").await();

    assertEquals(GROUP_NAME, state.name);
    assertEquals("updated-groupOf-description", state.description);
    assertEquals(GROUP_ID, state.id);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupDescriptionChanged.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  @Test
  public void assignGroup() {
    givenGroupIsProvisioned(GROUP_ID, GROUP_NAME, GROUP_DESCRIPTION);
    GroupState innerGroup = givenGroupIsProvisioned(GroupId.from(TENANT_ID, "Group B"), "Group B", "Group B description");

    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = groupOf(GROUP_ID).assignGroup(innerGroup.id).await();

    // @TODO implement assignGroup()
    assertEquals(GROUP_NAME, state.name);
    assertEquals(GROUP_DESCRIPTION, state.description);
    assertEquals(GROUP_ID, state.id);
    assertEquals(3, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupAssignedToGroup.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 2)).typeName());
  }

  @Test
  public void unassignGroup() {
    givenGroupIsProvisioned(GROUP_ID, GROUP_NAME, GROUP_DESCRIPTION);
    GroupState innerGroup = givenGroupIsProvisioned(GroupId.from(TENANT_ID, "Group B"), "Group B", "Group B description");

    groupOf(GROUP_ID).assignGroup(innerGroup.id).await();

    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = groupOf(GROUP_ID).unassignGroup(innerGroup.id).await();

    // @TODO implement unassignGroup()
    assertEquals(GROUP_NAME, state.name);
    assertEquals(GROUP_DESCRIPTION, state.description);
    assertEquals(GROUP_ID, state.id);
    assertEquals(4, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(GroupUnassignedFromGroup.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 3)).typeName());
  }

  @Test
  @Disabled("Requires user implementation")
  public void assignUser() {
    givenGroupIsProvisioned(GROUP_ID);
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = groupOf(GROUP_ID).assignUser("updated-groupOf-tenantId").await();

    assertEquals(state.name, "groupOf-name");
    assertEquals(state.description, "groupOf-description");
    assertEquals(state.id.tenantId, "updated-groupOf-tenantId");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserAssignedToGroup.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_UNASSIGN_USER_TEST = "updated-groupOf-tenantId";

  @Test
  @Disabled("Requires user implementation")
  public void unassignUser() {
    givenGroupIsProvisioned(GROUP_ID);
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupState state = groupOf(GROUP_ID).unassignUser(TENANT_ID_FOR_UNASSIGN_USER_TEST).await();

    assertEquals(state.name, "groupOf-name");
    assertEquals(state.description, "groupOf-description");
    assertEquals(state.id.tenantId, "updated-groupOf-tenantId");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserUnassignedFromGroup.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private Group groupOf(final GroupId groupId) {
    return world.actorFor(Group.class, GroupEntity.class, groupId);
  }

  private GroupState givenGroupIsProvisioned(final GroupId groupId) {
    return groupOf(groupId).provisionGroup(GROUP_NAME, GROUP_DESCRIPTION).await();
  }

  private GroupState givenGroupIsProvisioned(final GroupId groupId, final String name, final String description) {
    return groupOf(groupId).provisionGroup(name, description).await();
  }
}
