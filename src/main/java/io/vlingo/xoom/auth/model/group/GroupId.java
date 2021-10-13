package io.vlingo.xoom.auth.model.group;

import io.vlingo.xoom.auth.model.tenant.TenantId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class GroupId {
  public final TenantId tenantId;
  public final String groupName;

  public static GroupId from(final TenantId tenantId, final String groupName) {
    return new GroupId(tenantId, groupName);
  }

  private GroupId(final TenantId tenantId, final String groupName) {
    this.tenantId = tenantId;
    this.groupName = groupName;
  }

  public String idString() {
    return tenantId.id != "" || groupName != "" ? String.format("%s:%s", tenantId.id, groupName) : "";
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof GroupId)) {
      return false;
    }
    GroupId groupId = (GroupId) other;
    return tenantId.equals(groupId.tenantId) && groupName.equals(groupId.groupName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tenantId, groupName);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("tenantId", tenantId.idString())
            .append("groupName", groupName)
            .toString();
  }
}
