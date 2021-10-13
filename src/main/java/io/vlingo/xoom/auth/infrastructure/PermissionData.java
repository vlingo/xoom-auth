package io.vlingo.xoom.auth.infrastructure;

import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.stream.Collectors;
import java.util.*;

import io.vlingo.xoom.auth.model.permission.PermissionState;

@SuppressWarnings("all")
public class PermissionData {
  public final String id;
  public final Set<ConstraintData> constraints = new HashSet<>();
  public final String name;
  public final String description;
  public final String tenantId;

  public static PermissionData from(final PermissionState permissionState) {
    final Set<ConstraintData> constraints = permissionState.constraints != null ? permissionState.constraints.stream().map(ConstraintData::from).collect(java.util.stream.Collectors.toSet()) : new HashSet<>();
    return from(permissionState.id, constraints, permissionState.name, permissionState.description);
  }

  public static PermissionData from(final TenantId tenantId, final Set<ConstraintData> constraints, final String name, final String description) {
    return new PermissionData(tenantId, constraints, name, description);
  }

  public static PermissionData from(final PermissionId permissionId, final Set<ConstraintData> constraints, final String name, final String description) {
    return new PermissionData(permissionId, constraints, name, description);
  }

  public static List<PermissionData> fromAll(final List<PermissionState> states) {
    return states.stream().map(PermissionData::from).collect(Collectors.toList());
  }

  public static PermissionData empty() {
    return from(PermissionState.identifiedBy(PermissionId.from(TenantId.from(""), "")));
  }

  private PermissionData(final TenantId tenantId, final Set<ConstraintData> constraints, final String name, final String description) {
    this.id = null;
    this.constraints.addAll(constraints);
    this.name = name;
    this.description = description;
    this.tenantId = tenantId.idString();
  }

  private PermissionData(final PermissionId permissionId, final Set<ConstraintData> constraints, final String name, final String description) {
    this.id = permissionId.idString();
    this.constraints.addAll(constraints);
    this.name = name;
    this.description = description;
    this.tenantId = permissionId.tenantId.idString();
  }

  public PermissionState toPermissionState() {
    return new PermissionState(PermissionId.from(TenantId.from(tenantId), name), constraints.stream().map(ConstraintData::toConstraint).collect(java.util.stream.Collectors.toSet()), name, description);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    PermissionData another = (PermissionData) other;
    return new EqualsBuilder()
              .append(this.id, another.id)
              .append(this.constraints, another.constraints)
              .append(this.description, another.description)
              .append(this.name, another.name)
              .append(this.tenantId, another.tenantId)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("id", id)
              .append("constraints", constraints)
              .append("description", description)
              .append("name", name)
              .append("tenantId", tenantId)
              .toString();
  }

}
