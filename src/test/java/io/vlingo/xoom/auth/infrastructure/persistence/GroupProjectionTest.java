package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.group.*;
import io.vlingo.xoom.auth.model.role.GroupAssignedToRole;
import io.vlingo.xoom.auth.model.role.GroupUnassignedFromRole;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.role.RoleProvisioned;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.common.Completes;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static org.junit.jupiter.api.Assertions.*;

public class GroupProjectionTest extends ProjectionTest {

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

    assertCompletes(groupOf(groupIdA), group -> {
      assertEquals(setOf(Relation.groupWithMember(groupIdA, groupIdB)), group.groups);
      assertFalse(group.hasMember(groupIdA));
      assertTrue(group.hasMember(groupIdB));
    });
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

    assertCompletes(groupOf(groupIdA), group -> {
      assertEquals(setOf(Relation.groupWithMember(groupIdA, groupIdC)), group.groups);
      assertFalse(group.hasMember(groupIdA));
      assertFalse(group.hasMember(groupIdB));
      assertTrue(group.hasMember(groupIdC));
    });
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

    assertCompletes(groupOf(groupId), group -> {
      assertEquals(setOf(Relation.userAssignedToGroup(userId, groupId)), group.users);
      assertTrue(group.hasMember(userId));
    });
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

    assertCompletes(groupOf(groupId), group -> {
      assertEquals(setOf(Relation.userAssignedToGroup(userIdAlice, groupId)), group.users);
      assertTrue(group.hasMember(userIdAlice));
      assertFalse(group.hasMember(userIdBobby));
    });
  }

  @Test
  public void itProjectsGroupEventsToGroupView() {
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
      assertEquals(setOf(Relation.groupWithMember(groupIdA, groupIdB)), group.groups);
      assertEquals(setOf(Relation.userAssignedToGroup(userId, groupIdA)), group.users);
      assertEquals("Group A improved", group.description);
    });
  }

  @Test
  public void itProjectsGroupAssignedToRole() {
    final TenantId tenantId = TenantId.unique();
    final GroupId groupId = GroupId.from(tenantId, "group-a");
    final RoleId roleIdA = RoleId.from(tenantId, "role-a");
    final RoleId roleIdB = RoleId.from(tenantId, "role-b");

    givenEvents(
            new GroupProvisioned(groupId, "group-a", "Group A"),
            new RoleProvisioned(roleIdA, "role-a", "Role A"),
            new RoleProvisioned(roleIdB, "role-b", "Role B"),
            new GroupAssignedToRole(roleIdA, groupId),
            new GroupAssignedToRole(roleIdB, groupId)
    );

    assertCompletes(groupOf(groupId), group -> {
      assertEquals(setOf(Relation.groupAssignedToRole(groupId, roleIdA), Relation.groupAssignedToRole(groupId, roleIdB)), group.roles);
      assertTrue(group.isInRole(roleIdA));
      assertTrue(group.isInRole(roleIdB));
    });
  }

  @Test
  public void itProjectsGroupUnassignedFromRole() {
    final TenantId tenantId = TenantId.unique();
    final GroupId groupId = GroupId.from(tenantId, "group-a");
    final RoleId roleIdA = RoleId.from(tenantId, "role-a");
    final RoleId roleIdB = RoleId.from(tenantId, "role-b");

    givenEvents(
            new GroupProvisioned(groupId, "group-a", "Group A"),
            new RoleProvisioned(roleIdA, "role-a", "Role A"),
            new RoleProvisioned(roleIdB, "role-b", "Role B"),
            new GroupAssignedToRole(roleIdA, groupId),
            new GroupAssignedToRole(roleIdB, groupId),
            new GroupUnassignedFromRole(roleIdA, groupId)
    );

    assertCompletes(groupOf(groupId), group -> {
      assertEquals(setOf(Relation.groupAssignedToRole(groupId, roleIdB)), group.roles);
      assertFalse(group.isInRole(roleIdA));
      assertTrue(group.isInRole(roleIdB));
    });
  }

  private Completes<GroupView> groupOf(final GroupId groupId) {
    return world.actorFor(GroupQueries.class, GroupQueriesActor.class, stateStore).groupOf(groupId);
  }

  private <T> Set<T> setOf(T... elements) {
    return new HashSet<T>(Arrays.asList(elements));
  }
}
