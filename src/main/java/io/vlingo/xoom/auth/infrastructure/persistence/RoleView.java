package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.role.RoleState;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class RoleView {
  public final String id;
  public final String tenantId;
  public final String name;
  public final String description;

  public static RoleView empty() {
    return from(RoleState.identifiedBy(RoleId.from(TenantId.from(""), "")));
  }

  public static RoleView from(final RoleState roleState) {
    return from(roleState.roleId, roleState.name, roleState.description);
  }

  public static RoleView from(final TenantId tenantId, final String name, final String description) {
    return new RoleView(tenantId, name, description);
  }

  public static RoleView from(final RoleId roleId, final String name, final String description) {
    return new RoleView(roleId, name, description);
  }

  private RoleView(final TenantId tenantId, final String name, final String description) {
    this.id = null;
    this.tenantId = tenantId.idString();
    this.name = name;
    this.description = description;
  }

  private RoleView(final RoleId roleId, final String name, final String description) {
    this.id = roleId.idString();
    this.tenantId = roleId.tenantId.idString();
    this.name = name;
    this.description = description;
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
