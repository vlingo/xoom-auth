package io.vlingo.xoom.auth.model.tenant;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class TenantId {
  public final String id;

  public static TenantId from(final String tenantId) {
    return new TenantId(tenantId);
  }

  private TenantId(final String id) {
    this.id = id;
  }

  public String idString() {
    return id;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof TenantId)) {
      return false;
    }
    return id.equals(((TenantId) other).id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("id", id)
            .toString();
  }
}
