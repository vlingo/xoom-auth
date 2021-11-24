package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.user.UserId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Objects;

import static io.vlingo.xoom.auth.infrastructure.persistence.Relation.TYPE.*;

public class Relation<L, R> {
  public final L left;
  public final TYPE type;
  public final R right;

  public static Relation<GroupId, GroupId> groupWithMember(final GroupId parentGroup, final GroupId childGroup) {
    return new Relation<>(parentGroup, GROUP_HAS_MEMBER, childGroup);
  }

  public static Relation<GroupId, GroupId> groupWithParent(final GroupId childGroup, final GroupId parentGroup) {
    return new Relation<>(childGroup, GROUP_HAS_PARENT, parentGroup);
  }

  public static Relation<RoleId, PermissionId> roleWithPermission(final RoleId role, final PermissionId permission) {
    return new Relation<>(role, ROLE_HAS_PERMISSION, permission);
  }

  public static Relation<PermissionId, RoleId> permissionAttachedToRole(final PermissionId permission, final RoleId role) {
    return new Relation<>(permission, PERMISSION_IS_ATTACHED_TO_ROLE, role);
  }

  public static Relation<RoleId, GroupId> roleWithGroup(final RoleId role, final GroupId group) {
    return new Relation<>(role, ROLE_WITH_GROUP, group);
  }

  public static Relation<GroupId, RoleId> groupAssignedToRole(final GroupId group, final RoleId role) {
    return new Relation<>(group, GROUP_ASSIGNED_TO_ROLE, role);
  }

  public static Relation<UserId, RoleId> userAssignedToRole(final UserId user, final RoleId role) {
    return new Relation<>(user, USER_ASSIGNED_TO_ROLE, role);
  }

  public static Relation<RoleId, UserId> roleWithUser(final RoleId role, final UserId user) {
    return new Relation<>(role, ROLE_WITH_USER, user);
  }

  public static Relation<UserId, GroupId> userAssignedToGroup(final UserId user, final GroupId group) {
    return new Relation<>(user, USER_ASSIGNED_TO_GROUP, group);
  }

  public static Relation<GroupId, UserId> groupWithMember(final GroupId group, final UserId user) {
    return new Relation<>(group, GROUP_HAS_USER_MEMBER, user);
  }

  enum TYPE {
    GROUP_HAS_MEMBER,
    GROUP_HAS_PARENT,
    ROLE_HAS_PERMISSION,
    PERMISSION_IS_ATTACHED_TO_ROLE,
    GROUP_ASSIGNED_TO_ROLE,
    ROLE_WITH_GROUP,
    USER_ASSIGNED_TO_ROLE,
    ROLE_WITH_USER,
    USER_ASSIGNED_TO_GROUP,
    GROUP_HAS_USER_MEMBER
  }

  private Relation(final L left, final TYPE type, final R right) {
    this.left = left;
    this.type = type;
    this.right = right;
  }

  public Relation<R, L> invert() {
    final HashMap<TYPE, TYPE> inversions = new HashMap<TYPE, TYPE>() {{
      put(GROUP_HAS_MEMBER, GROUP_HAS_PARENT);
      put(GROUP_HAS_PARENT, GROUP_HAS_MEMBER);
      put(ROLE_HAS_PERMISSION, PERMISSION_IS_ATTACHED_TO_ROLE);
      put(PERMISSION_IS_ATTACHED_TO_ROLE, ROLE_HAS_PERMISSION);
      put(GROUP_ASSIGNED_TO_ROLE, ROLE_WITH_GROUP);
      put(ROLE_WITH_GROUP, GROUP_ASSIGNED_TO_ROLE);
      put(USER_ASSIGNED_TO_ROLE, ROLE_WITH_USER);
      put(ROLE_WITH_USER, USER_ASSIGNED_TO_ROLE);
      put(USER_ASSIGNED_TO_GROUP, GROUP_HAS_USER_MEMBER);
      put(GROUP_HAS_USER_MEMBER, USER_ASSIGNED_TO_GROUP);
    }};

    return new Relation<>(right, inversions.get(type), left);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Relation)) return false;
    Relation<?, ?> relation = (Relation<?, ?>) o;
    return left.equals(relation.left) && type == relation.type && right.equals(relation.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, type, right);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("left", left)
            .append("type", type.name())
            .append("right", right)
            .toString();
  }
}
