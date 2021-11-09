package io.vlingo.xoom.auth.infrastructure;

import io.vlingo.xoom.auth.model.tenant.TenantId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("all")
public class PermissionData {
  public final String id;
  public final Set<ConstraintData> constraints = new HashSet<>();
  public final String name;
  public final String description;
  public final String tenantId;

  public static PermissionData from(final TenantId tenantId, final Set<ConstraintData> constraints, final String name, final String description) {
    return new PermissionData(tenantId, constraints, name, description);
  }

  private PermissionData(final TenantId tenantId, final Set<ConstraintData> constraints, final String name, final String description) {
    this.id = null;
    this.constraints.addAll(constraints);
    this.name = name;
    this.description = description;
    this.tenantId = tenantId.idString();
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
