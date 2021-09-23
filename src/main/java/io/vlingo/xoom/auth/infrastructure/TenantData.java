package io.vlingo.xoom.auth.infrastructure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.stream.Collectors;
import io.vlingo.xoom.auth.model.tenant.TenantState;
import java.util.*;

@SuppressWarnings("all")
public class TenantData {
  public final String tenantId;
  public final String name;
  public final String description;
  public final boolean active;

  public static TenantData from(final TenantState tenantState) {
    return from(tenantState.id, tenantState.name, tenantState.description, tenantState.active);
  }

  public static TenantData from(final String tenantId, final String name, final String description, final boolean active) {
    return new TenantData(tenantId, name, description, active);
  }

  public static TenantData from(final String name, final String description, final boolean active) {
    return from(UUID.randomUUID().toString(), name, description, active);
  }

  public static List<TenantData> fromAll(final List<TenantState> states) {
    return states.stream().map(TenantData::from).collect(Collectors.toList());
  }

  public static TenantData empty() {
    return from(TenantState.identifiedBy(""));
  }

  private TenantData (final String tenantId, final String name, final String description, final boolean active) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.active = active;
  }

  public TenantState toTenantState() {
    return new TenantState(tenantId, name, description, active);
  }

  public TenantData withTenantId(String tenantId) {
    return from(tenantId, this.name, this.description, this.active);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    TenantData another = (TenantData) other;
    return new EqualsBuilder()
              .append(this.tenantId, another.tenantId)
              .append(this.name, another.name)
              .append(this.description, another.description)
              .append(this.active, another.active)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("id", tenantId)
              .append("name", name)
              .append("description", description)
              .append("active", active)
              .toString();
  }
}
