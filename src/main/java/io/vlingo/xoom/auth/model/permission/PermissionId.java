package io.vlingo.xoom.auth.model.permission;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class PermissionId {
  public final String tenantId;
  public final String permissionName;

  public static PermissionId from(final String tenantId, final String permissionName) {
    return new PermissionId(tenantId, permissionName);
  }

  private PermissionId(final String tenantId, final String permissionName) {
    this.tenantId = tenantId;
    this.permissionName = permissionName;
  }

  public String idString() {
    return tenantId != "" || permissionName != "" ? String.format("%s:%s", tenantId, permissionName) : "";
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof PermissionId)) {
      return false;
    }
    PermissionId permissionName = (PermissionId) other;
    return tenantId.equals(permissionName.tenantId) && this.permissionName.equals(permissionName.permissionName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tenantId, permissionName);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("tenantId", tenantId)
            .append("permissionName", permissionName)
            .toString();
  }
}
