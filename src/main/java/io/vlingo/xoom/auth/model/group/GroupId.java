package io.vlingo.xoom.auth.model.group;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class GroupId {
  public final String tenantId;
  public final String groupName;

  public static GroupId from(final String tenantId, final String groupName) {
    return new GroupId(tenantId, groupName);
  }

  private GroupId(final String tenantId, final String groupName) {
    this.tenantId = tenantId;
    this.groupName = groupName;
  }

  public String idString() {
    return tenantId != "" || groupName != "" ? String.format("%s:%s", tenantId, groupName) : "";
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
            .append("tenantId", tenantId)
            .append("groupName", groupName)
            .toString();
  }
}
