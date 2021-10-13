package io.vlingo.xoom.auth.infrastructure;

import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.role.RoleState;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class RoleData {
  public final String id;
  public final String tenantId;
  public final String name;
  public final String description;

  public static RoleData from(final RoleState roleState) {
    return from(roleState.roleId, roleState.name, roleState.description);
  }

  public static RoleData from(final TenantId tenantId, final String name, final String description) {
    return new RoleData(tenantId, name, description);
  }

  public static RoleData from(final RoleId roleId, final String name, final String description) {
    return new RoleData(roleId, name, description);
  }

  public static List<RoleData> fromAll(final List<RoleState> states) {
    return states.stream().map(RoleData::from).collect(Collectors.toList());
  }

  public static RoleData empty() {
    return from(RoleState.identifiedBy(RoleId.from(TenantId.from(""), "")));
  }

  private RoleData(final TenantId tenantId, final String name, final String description) {
    this.id = null;
    this.tenantId = tenantId.idString();
    this.name = name;
    this.description = description;
  }

  private RoleData(final RoleId roleId, final String name, final String description) {
    this.id = roleId.idString();
    this.tenantId = roleId.tenantId.idString();
    this.name = name;
    this.description = description;
  }

  public RoleState toRoleState() {
    return new RoleState(RoleId.from(TenantId.from(tenantId), name), name, description);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    RoleData another = (RoleData) other;
    return new EqualsBuilder()
              .append(this.id, another.id)
              .append(this.tenantId, another.tenantId)
              .append(this.name, another.name)
              .append(this.description, another.description)
              .isEquals();
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
