package io.vlingo.xoom.auth.infrastructure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
    return from(groupState.id, groupState.name, groupState.description, groupState.tenantId);
  }

  public static GroupData from(final String id, final String name, final String description, final String tenantId) {
    return new GroupData(id, name, description, tenantId);
  }

  public static List<GroupData> from(final List<GroupState> states) {
    return states.stream().map(GroupData::from).collect(Collectors.toList());
  }

  public static GroupData empty() {
    return from(GroupState.identifiedBy(""));
  }

  private GroupData (final String id, final String name, final String description, final String tenantId) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.tenantId = tenantId;
  }

  public GroupState toGroupState() {
    return new GroupState(id, name, description, tenantId);
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
