package io.vlingo.xoom.auth.infrastructure;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.group.GroupState;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class GroupData {
  public final String id;
  public final String name;
  public final String description;
  public final String tenantId;

  public static GroupData from(final TenantId tenantId, final String name, final String description) {
    return new GroupData(tenantId, name, description);
  }

  private GroupData(final TenantId tenantId, final String name, final String description) {
    this.id = null;
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
