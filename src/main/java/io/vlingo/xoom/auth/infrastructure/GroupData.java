package io.vlingo.xoom.auth.infrastructure;

import io.vlingo.xoom.auth.model.group.GroupId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.stream.Collectors;
import java.util.*;
import io.vlingo.xoom.auth.model.group.GroupState;

@SuppressWarnings("all")
public class GroupData {
  public final String id;
  public final String name;
  public final String description;
  public final String tenantId;

  public static GroupData from(final GroupState groupState) {
    return from(groupState.id, groupState.name, groupState.description);
  }

  @Deprecated
  public static GroupData from(final String id, final String name, final String description, final String tenantId) {
    return new GroupData(id, name, description, tenantId);
  }

  public static GroupData from(final GroupId id, final String name, final String description) {
    return new GroupData(id, name, description);
  }

  public static List<GroupData> fromAll(final List<GroupState> states) {
    return states.stream().map(GroupData::from).collect(Collectors.toList());
  }

  public static GroupData empty() {
    return from(GroupState.identifiedBy(GroupId.from("", "")));
  }

  private GroupData(final GroupId groupId, final String name, final String description) {
    this.id = groupId.idString();
    this.name = name;
    this.description = description;
    this.tenantId = groupId.tenantId;
  }

  @Deprecated
  private GroupData(final String groupId, final String name, final String description, final String tenantId) {
    this.id = groupId;
    this.name = name;
    this.description = description;
    this.tenantId = tenantId;
  }

  public GroupState toGroupState() {
    return new GroupState(GroupId.from(tenantId, name), name, description);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    GroupData another = (GroupData) other;
    return new EqualsBuilder()
              .append(this.id, another.id)
              .append(this.name, another.name)
              .append(this.description, another.description)
              .append(this.tenantId, another.tenantId)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("id", id)
              .append("name", name)
              .append("description", description)
              .append("tenantId", tenantId)
              .toString();
  }

}
