package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.role.RoleState;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RoleView {
  public final String id;
  public final String tenantId;
  public final String name;
  public final String description;
  public final Set<Relation<PermissionId, RoleId>> permissions;
  public final Set<Relation<GroupId, RoleId>> groups;
  public final Set<Relation<UserId, RoleId>> users;

  public static RoleView empty() {
    return from(TenantId.from(""), "", "");
  }

  public static RoleView from(final RoleState roleState) {
    return from(roleState.roleId, roleState.name, roleState.description);
  }

  public static RoleView from(final TenantId tenantId, final String name, final String description) {
    return new RoleView(tenantId, name, description, new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  public static RoleView from(final RoleId roleId, final String name, final String description) {
    return new RoleView(roleId, name, description, new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  public static RoleView from(final RoleId roleId, final String name, final String description, final Set<Relation<PermissionId, RoleId>> permissions, final Set<Relation<GroupId, RoleId>> groups, final Set<Relation<UserId, RoleId>> users) {
    return new RoleView(roleId, name, description, permissions, groups, users);
  }

  private RoleView(final TenantId tenantId, final String name, final String description, final Set<Relation<PermissionId, RoleId>> permissions, final Set<Relation<GroupId, RoleId>> groups, final Set<Relation<UserId, RoleId>> users) {
    this.id = null;
    this.tenantId = tenantId.idString();
    this.name = name;
    this.description = description;
    this.permissions = permissions;
    this.groups = groups;
    this.users = users;
  }

  private RoleView(final RoleId roleId, final String name, final String description, final Set<Relation<PermissionId, RoleId>> permissions, final Set<Relation<GroupId, RoleId>> groups, final Set<Relation<UserId, RoleId>> users) {
    this.id = roleId.idString();
    this.tenantId = roleId.tenantId.idString();
    this.name = name;
    this.description = description;
    this.permissions = permissions;
    this.groups = groups;
    this.users = users;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RoleView)) return false;
    RoleView roleView = (RoleView) o;
    return id.equals(roleView.id) && tenantId.equals(roleView.tenantId) && name.equals(roleView.name) && description.equals(roleView.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, tenantId, name, description);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("id", id)
            .append("tenantId", tenantId)
            .append("name", name)
            .append("description", description)
            .toString();
  }
}
