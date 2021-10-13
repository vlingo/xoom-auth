package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.auth.model.tenant.TenantId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class UserId {
  public final TenantId tenantId;
  public final String username;

  public static UserId from(final TenantId tenantId, final String username) {
    return new UserId(tenantId, username);
  }

  private UserId(final TenantId tenantId, final String username) {
    this.tenantId = tenantId;
    this.username = username;
  }

  public String idString() {
    return tenantId.id != "" || username != "" ? String.format("%s:%s", tenantId.id, username) : "";
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof UserId)) {
      return false;
    }
    UserId otherUserId = (UserId) other;
    return tenantId.equals(otherUserId.tenantId) && this.username.equals(otherUserId.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tenantId, username);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("tenantId", tenantId)
            .append("username", username)
            .toString();
  }
}