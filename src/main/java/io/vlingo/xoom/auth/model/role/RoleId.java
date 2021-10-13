package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.auth.model.tenant.TenantId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class RoleId {
  public final TenantId tenantId;
  public final String roleName;

  public static RoleId from(final TenantId tenantId, final String roleName) {
    return new RoleId(tenantId, roleName);
  }

  private RoleId(final TenantId tenantId, final String roleName) {
    this.tenantId = tenantId;
    this.roleName = roleName;
  }

  public String idString() {
    return tenantId.id != "" || roleName != "" ? String.format("%s:%s", tenantId.id, roleName) : "";
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof RoleId)) {
      return false;
    }
    RoleId otherRoleId = (RoleId) other;
    return tenantId.equals(otherRoleId.tenantId) && this.roleName.equals(otherRoleId.roleName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tenantId, roleName);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("tenantId", tenantId)
            .append("roleName", roleName)
            .toString();
  }
}
