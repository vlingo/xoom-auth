package io.vlingo.xoom.auth.model.group;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.auth.model.value.EncodedMember;
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

public class GroupEntityTest {

  private final TenantId TENANT_ID = TenantId.from("7c7161d4-12c9-4dde-9e8f-26c40bfc7902");
  private final String GROUP_NAME = "Group A";
  private final String GROUP_DESCRIPTION = "Group A description";
  private final GroupId GROUP_ID = GroupId.from(TENANT_ID, GROUP_NAME);

  private World world;
  private MockDispatcher dispatcher;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp() {
    world = World.startWithDefaults("test-es");
    dispatcher = new MockDispatcher();

    final EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);
    entryAdapterProvider.registerAdapter(GroupProvisioned.class, new GroupProvisionedAdapter());
    entryAdapterProvider.registerAdapter(GroupDescriptionChanged.class, new GroupDescriptionChangedAdapter());
    entryAdapterProvider.registerAdapter(GroupAssignedToGroup.class, new GroupAssignedToGroupAdapter());
    entryAdapterProvider.registerAdapter(GroupUnassignedFromGroup.class, new GroupUnassignedFromGroupAdapter());
    entryAdapterProvider.registerAdapter(UserAssignedToGroup.class, new UserAssignedToGroupAdapter());
    entryAdapterProvider.registerAdapter(UserUnassignedFromGroup.class, new UserUnassignedFromGroupAdapter());

    final Journal<String> journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, GroupEntity.class, GroupEntity.class.getSimpleName()));
  }

  @Test
  public void groupIsProvisionedWithNameAndDescription() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Completes<GroupState> outcome = groupOf(GROUP_ID).provisionGroup(GROUP_NAME, GROUP_DESCRIPTION);

    assertCompletes(outcome, state -> {
      assertEquals(GROUP_NAME, state.name);
      assertEquals(GROUP_DESCRIPTION, state.description);
      assertEquals(GROUP_ID, state.id);
      assertEventDispatched(dispatcherAccess, 1, GroupProvisioned.class);
    });
  }

  @Test
  public void groupDescriptionIsChanged() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<GroupState> outcome = givenGroupIsProvisioned(GROUP_ID, GROUP_NAME, GROUP_DESCRIPTION)
            .andThenTo(g -> groupOf(GROUP_ID).changeDescription("updated-groupOf-description"));

    assertCompletes(outcome, state -> {
      assertEquals(GROUP_NAME, state.name);
      assertEquals("updated-groupOf-description", state.description);
      assertEquals(GROUP_ID, state.id);
      assertEventDispatched(dispatcherAccess, 2, GroupDescriptionChanged.class);
    });
  }

  @Test
  public void groupIsAssignedAnotherGroup() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupId innerGroupId = GroupId.from(TENANT_ID, "Group B");

    final Completes<GroupState> outcome = givenGroupIsProvisioned(GROUP_ID, GROUP_NAME, GROUP_DESCRIPTION)
            .andThenTo(g -> givenGroupIsProvisioned(innerGroupId, "Group B", "Group B description"))
            .andThenTo(g -> groupOf(GROUP_ID).assignGroup(innerGroupId));

    assertCompletes(outcome, state -> {
      assertContainsMember(EncodedMember.group(innerGroupId), state);
      assertEventDispatched(dispatcherAccess, 3, GroupAssignedToGroup.class);
    });
  }

  @Test
  public void groupIsUnassignedFromAnotherGroup() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final GroupId groupBId = GroupId.from(TENANT_ID, "Group B");
    final GroupId groupCId = GroupId.from(TENANT_ID, "Group C");

    final Completes<GroupState> outcome = givenGroupIsProvisioned(GROUP_ID, GROUP_NAME, GROUP_DESCRIPTION)
            .andThenTo(g -> givenGroupIsProvisioned(groupBId, "Group B", "Group B description"))
            .andThenTo(g -> givenGroupIsProvisioned(groupBId, "Group C", "Group C description"))
            .andThenTo(g -> groupOf(GROUP_ID).assignGroup(groupBId))
            .andThenTo(g -> groupOf(GROUP_ID).assignGroup(groupCId))
            .andThenTo(g -> groupOf(GROUP_ID).unassignGroup(groupBId));

    assertCompletes(outcome, state -> {
      assertNotContainsMember(EncodedMember.group(groupBId), state);
      assertContainsMember(EncodedMember.group(groupCId), state);
      assertEventDispatched(dispatcherAccess, 6, GroupUnassignedFromGroup.class);
    });
  }

  @Test
  public void userIsAssignedToAGroup() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final UserId userId = UserId.from(TenantId.unique(), "bobby");

    final Completes<GroupState> outcome = givenGroupIsProvisioned(GROUP_ID)
            .andThenTo(g -> groupOf(GROUP_ID).assignUser(userId));

    assertCompletes(outcome, state -> {
      assertContainsMember(EncodedMember.user(userId), state);
      assertEventDispatched(dispatcherAccess, 2, UserAssignedToGroup.class);
    });
  }

  @Test
  public void userIsUnassignedFromAGroup() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final UserId firstUserId = UserId.from(TenantId.unique(), "bobby");
    final UserId secondUserId = UserId.from(TenantId.unique(), "alice");

    final Completes<GroupState> outcome = givenGroupIsProvisioned(GROUP_ID)
            .andThenTo(g -> groupOf(GROUP_ID).assignUser(firstUserId))
            .andThenTo(g -> groupOf(GROUP_ID).assignUser(secondUserId))
            .andThenTo(g -> groupOf(GROUP_ID).unassignUser(firstUserId));

    assertCompletes(outcome, state -> {
      assertNotContainsMember(EncodedMember.user(firstUserId), state);
      assertContainsMember(EncodedMember.user(secondUserId), state);
      assertEventDispatched(dispatcherAccess, 4, UserUnassignedFromGroup.class);
    });
  }

  private Group groupOf(final GroupId groupId) {
    return world.actorFor(Group.class, GroupEntity.class, groupId);
  }

  private Completes<GroupState> givenGroupIsProvisioned(final GroupId groupId) {
    return groupOf(groupId).provisionGroup(GROUP_NAME, GROUP_DESCRIPTION);
  }

  private Completes<GroupState> givenGroupIsProvisioned(final GroupId groupId, final String name, final String description) {
    return groupOf(groupId).provisionGroup(name, description);
  }

  private void assertContainsMember(final EncodedMember member, final GroupState state) {
    assertEquals(new HashSet<>(Collections.singletonList(member)), state.members);
  }

  private void assertNotContainsMember(final EncodedMember member, final GroupState state) {
    state.members.forEach(m -> assertNotEquals(m, member));
  }
}
