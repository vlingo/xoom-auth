package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.auth.model.permission.PermissionState;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.value.Constraint;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PermissionView {
  public final String id;
  public final String tenantId;
  public final String name;
  public final String description;
  public final Set<ConstraintView> constraints;
  public final Set<Relation<RoleId, PermissionId>> roles;

  public static PermissionView empty() {
    return from(PermissionId.from(TenantId.from(""), ""), new HashSet<>(), "", "", new HashSet<>());
  }

  public static PermissionView from(final PermissionState permissionState) {
    final Set<ConstraintView> constraints = permissionState.constraints != null ? permissionState.constraints.stream().map(ConstraintView::from).collect(java.util.stream.Collectors.toSet()) : new HashSet<>();
    return from(permissionState.id, constraints, permissionState.name, permissionState.description, new HashSet<>());
  }

  public static PermissionView from(final TenantId tenantId, final Set<ConstraintView> constraints, final String name, final String description, final Set<Relation<RoleId, PermissionId>> roles) {
    return new PermissionView(null, tenantId.idString(), constraints, name, description, roles);
  }

  public static PermissionView from(final PermissionId permissionId, final Set<ConstraintView> constraints, final String name, final String description, final Set<Relation<RoleId, PermissionId>> roles) {
    return new PermissionView(permissionId.idString(), permissionId.tenantId.idString(), constraints, name, description, roles);
  }

  private PermissionView(final String id, final String tenantId, final Set<ConstraintView> constraints, final String name, final String description, final Set<Relation<RoleId, PermissionId>> roles) {
    this.id = id;
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.constraints = constraints;
    this.roles = roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PermissionView)) return false;
    PermissionView that = (PermissionView) o;
    return id.equals(that.id) && tenantId.equals(that.tenantId) && name.equals(that.name) && description.equals(that.description) && constraints.equals(that.constraints) && roles.equals(that.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, tenantId, name, description, constraints, roles);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("id", id)
            .append("tenantId", tenantId)
            .append("constraints", constraints)
            .append("description", description)
            .append("name", name)
            .append("roles", roles)
            .toString();
  }

  public boolean isAttachedTo(final RoleId roleId) {
    return roles.stream().filter(r -> r.left.equals(roleId)).findFirst().isPresent();
  }

  public static class ConstraintView {
    public final String type;
    public final String name;
    public final String value;
    public final String description;

    public static ConstraintView from(final Constraint constraint) {
      if (constraint == null) {
        return ConstraintView.empty();
      } else {
        return from(constraint.type.name(), constraint.name, constraint.value, constraint.description);
      }
    }

    public static ConstraintView from(final String type, final String name,  final String value, final String description) {
      return new ConstraintView(type, name, value, description);
    }

    public static ConstraintView empty() {
      return new ConstraintView(null, null, null, null);
    }

    private ConstraintView(String type, String name, String value, String description) {
      this.type = type;
      this.name = name;
      this.value = value;
      this.description = description;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ConstraintView)) return false;
      ConstraintView that = (ConstraintView) o;
      return type.equals(that.type) && name.equals(that.name) && Objects.equals(value, that.value) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, name, value, description);
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("description", description)
              .append("name", name)
              .append("type", type)
              .append("value", value)
              .toString();
    }
  }
}
