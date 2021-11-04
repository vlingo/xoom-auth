package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.GroupData;
import io.vlingo.xoom.auth.model.group.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.projection.Projection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupProjectionTest extends ProjectionTest {

  @Override
  protected Set<Class<?>> statefulTypes() {
    return Collections.singleton(GroupData.class);
  }

  @Override
  protected Projection projection() {
    return world.actorFor(Projection.class, GroupProjectionActor.class, stateStore);
  }

  @Test
  public void itProjectsProvisionedGroup() {
    final GroupId groupId = GroupId.from(TenantId.unique(), "group-a");

    givenEvents(
            new GroupProvisioned(groupId, "group-a", "Group A description")
    );

    assertCompletes(groupOf(groupId), group -> {
      assertEquals(groupId.idString(), group.id);
      assertEquals(groupId.tenantId.idString(), group.tenantId);
      assertEquals("group-a", group.name);
      assertEquals("Group A description", group.description);
    });
  }

  @Test
  public void itProjectsDescriptionChange() {
    final GroupId groupId = GroupId.from(TenantId.unique(), "group-a");

    givenEvents(
            new GroupProvisioned(groupId, "group-a", "Group A description"),
            new GroupDescriptionChanged(groupId, "Group A improved")
    );

    assertCompletes(groupOf(groupId), group -> {
      assertEquals("group-a", group.name);
      assertEquals("Group A improved", group.description);
    });
  }

  @Test
  public void itProjectsGroupAssignedToAnotherGroup() {
    final GroupId groupIdA = GroupId.from(TenantId.unique(), "group-a");
    final GroupId groupIdB = GroupId.from(TenantId.unique(), "group-b");

    givenEvents(
            new GroupProvisioned(groupIdA, "group-a", "Group A"),
            new GroupProvisioned(groupIdB, "group-b", "Group B"),
            new GroupAssignedToGroup(groupIdA, groupIdB)
    );

    assertCompletes(groupOf(groupIdA), group -> assertEquals(setOf(groupIdB.idString()), group.groups));
  }

  @Test
  public void itProjectsGroupUnassignedFromAnotherGroup() {
    final GroupId groupIdA = GroupId.from(TenantId.unique(), "group-a");
    final GroupId groupIdB = GroupId.from(TenantId.unique(), "group-b");
    final GroupId groupIdC = GroupId.from(TenantId.unique(), "group-c");

    givenEvents(
            new GroupProvisioned(groupIdA, "group-a", "Group A"),
            new GroupProvisioned(groupIdB, "group-b", "Group B"),
            new GroupProvisioned(groupIdC, "group-c", "Group C"),
            new GroupAssignedToGroup(groupIdA, groupIdB),
            new GroupAssignedToGroup(groupIdA, groupIdC),
            new GroupUnassignedFromGroup(groupIdA, groupIdB)
    );

    assertCompletes(groupOf(groupIdA), group -> assertEquals(setOf(groupIdC.idString()), group.groups));
  }

  @Test
  public void itProjectsUserAssignedToGroup() {
    final TenantId tenantId = TenantId.unique();
    final GroupId groupId = GroupId.from(tenantId, "group-a");
    final UserId userId = UserId.from(tenantId, "bobby");

    givenEvents(
            new GroupProvisioned(groupId, "group-a", "Group A"),
            new UserAssignedToGroup(groupId, userId)
    );

    assertCompletes(groupOf(groupId), group -> assertEquals(setOf(userId.idString()), group.users));
  }

  @Test
  public void itProjectsUserUnassignedFromGroup() {
    final TenantId tenantId = TenantId.unique();
    final GroupId groupId = GroupId.from(tenantId, "group-a");
    final UserId userIdBobby = UserId.from(tenantId, "bobby");
    final UserId userIdAlice = UserId.from(tenantId, "alice");

    givenEvents(
            new GroupProvisioned(groupId, "group-a", "Group A"),
            new UserAssignedToGroup(groupId, userIdBobby),
            new UserAssignedToGroup(groupId, userIdAlice),
            new UserUnassignedFromGroup(groupId, userIdBobby)
    );

    assertCompletes(groupOf(groupId), group -> assertEquals(setOf(userIdAlice.idString()), group.users));
  }

  @Test
  public void itProjectsGroupEventsToGroupData() {
    final TenantId tenantId = TenantId.unique();
    final GroupId groupIdA = GroupId.from(tenantId, "group-a");
    final GroupId groupIdB = GroupId.from(TenantId.unique(), "group-b");
    final UserId userId = UserId.from(tenantId, "bobby");

    givenEvents(
            new GroupProvisioned(groupIdA, "group-a", "Group A"),
            new GroupProvisioned(groupIdB, "group-b", "Group B"),
            new GroupAssignedToGroup(groupIdA, groupIdB),
            new UserAssignedToGroup(groupIdA, userId),
            new GroupDescriptionChanged(groupIdA, "Group A improved")
    );

    assertCompletes(groupOf(groupIdA), group -> {
      assertEquals(setOf(groupIdB.idString()), group.groups);
      assertEquals(setOf(userId.idString()), group.users);
      assertEquals("Group A improved", group.description);
    });
  }

  private Completes<GroupView> groupOf(final GroupId groupId) {
    return world.actorFor(GroupQueries.class, GroupQueriesActor.class, stateStore).groupOf(groupId);
  }

  private <T> Set<T> setOf(T... elements) {
    return new HashSet<T>(Arrays.asList(elements));
  }
}
