package io.vlingo.xoom.auth.infrastructure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.stream.Collectors;
import io.vlingo.xoom.auth.model.tenant.TenantState;
import java.util.*;

@SuppressWarnings("all")
public class TenantData {
  public final String id;
  public final String name;
  public final String description;
  public final boolean active;

  public static TenantData from(final TenantState tenantState) {
    return from(tenantState.id, tenantState.name, tenantState.description, tenantState.active);
  }

  public static TenantData from(final String id, final String name, final String description, final boolean active) {
    return new TenantData(id, name, description, active);
  }

  public static List<TenantData> from(final List<TenantState> states) {
    return states.stream().map(TenantData::from).collect(Collectors.toList());
  }

  public static TenantData empty() {
    return from(TenantState.identifiedBy(""));
  }

  private TenantData (final String id, final String name, final String description, final boolean active) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.active = active;
  }

  public TenantState toTenantState() {
    return new TenantState(id, name, description, active);
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
              .append(this.id, another.id)
              .append(this.name, another.name)
              .append(this.description, another.description)
              .append(this.active, another.active)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("id", id)
              .append("name", name)
              .append("description", description)
              .append("active", active)
              .toString();
  }

}
