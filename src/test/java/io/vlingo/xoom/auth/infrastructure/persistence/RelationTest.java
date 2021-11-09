package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RelationTest {

  public static final TenantId TENANT_ID = TenantId.unique();

  @Test
  public void itInvertsTheGroupToGroupRelation() {
    final GroupId parentGroupId = GroupId.from(TENANT_ID, "group-a");
    final GroupId childGroupId = GroupId.from(TENANT_ID, "group-b");

    final Relation relation = Relation.groupWithMember(parentGroupId, childGroupId);
    final Relation invertedRelation = Relation.groupWithParent(childGroupId, parentGroupId);

    assertEquals(invertedRelation, relation.invert());
    assertEquals(relation, invertedRelation.invert());
  }

  @Test
  public void itInvertsTheRoleToPermissionRelation() {
    final RoleId roleId = RoleId.from(TENANT_ID, "role-a");
    final PermissionId permissionId = PermissionId.from(TENANT_ID, "permission-a");

    final Relation relation = Relation.roleWithPermission(roleId, permissionId);
    final Relation invertedRelation = Relation.permissionAttachedToRole(permissionId, roleId);

    assertEquals(invertedRelation, relation.invert());
    assertEquals(relation, invertedRelation.invert());
  }

  @Test
  public void itInvertsTheRoleToGroupRelation() {
    final RoleId roleId = RoleId.from(TENANT_ID, "role-a");
    final GroupId groupId = GroupId.from(TENANT_ID, "group-a");

    final Relation relation = Relation.roleWithGroup(roleId, groupId);
    final Relation invertedRelation = Relation.groupAssignedToRole(groupId, roleId);

    assertEquals(invertedRelation, relation.invert());
    assertEquals(relation, invertedRelation.invert());
  }


  @Test
  public void itInvertsTheRoleToUserRelation() {
    final RoleId roleId = RoleId.from(TENANT_ID, "role-a");
    final UserId userId = UserId.from(roleId.tenantId, "alice");

    final Relation relation = Relation.userAssignedToRole(userId, roleId);
    final Relation invertedRelation = Relation.roleWithUser(roleId, userId);

    assertEquals(invertedRelation, relation.invert());
    assertEquals(relation, invertedRelation.invert());
  }
}
