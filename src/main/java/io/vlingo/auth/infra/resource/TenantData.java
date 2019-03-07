// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import java.util.ArrayList;
import java.util.Collection;

import io.vlingo.auth.model.Tenant;

public final class TenantData {
  public final boolean active;
  public final String description;
  public final String name;
  public final String tenantId;

  public static TenantData from(final String name, final String description, final boolean active) {
    return new TenantData(name, description, active);
  }

  public static TenantData from(final String tenantId, final String name, final String description, final boolean active) {
    return new TenantData(tenantId, name, description, active);
  }

  public static TenantData from(final Tenant tenant) {
    return new TenantData(tenant.tenantId().value, tenant.name(), tenant.description(), tenant.isActive());
  }

  public static Collection<TenantData> from(final Collection<Tenant> tenants) {
    final Collection<TenantData> data = new ArrayList<>(tenants.size());
    for (final Tenant tenant : tenants) {
      data.add(from(tenant));
    }
    return data;
  }

  public TenantData(final String name, final String description, final boolean active) {
    this(null, name, description, active);
  }

  public TenantData(final String tenantId, final String name, final String description, final boolean active) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.active = active;
  }

  public TenantData withTenantId(final String tenantId) {
    return new TenantData(tenantId, this.name, this.description, this.active);
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != TenantData.class) {
      return false;
    }
    
    final TenantData otherTenantData = (TenantData) other;
    
    if (this.tenantId == null && otherTenantData.tenantId == null) {
      return this.name.equals(otherTenantData.name) &&
             this.description.equals(otherTenantData.description) &&
             this.active == otherTenantData.active;
    } else if (this.tenantId != null) {
      return this.tenantId.equals(otherTenantData.tenantId) &&
             this.name.equals(otherTenantData.name) &&
             this.description.equals(otherTenantData.description) &&
             this.active == otherTenantData.active;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    final int tenantIdHashCode = this.tenantId != null ? this.tenantId.hashCode() : 0;
    return 31 * (tenantIdHashCode + this.name.hashCode() + this.description.hashCode() + (active ? 1 : 0));
  }

  @Override
  public String toString() {
    return "TenantData[tenantId=" + tenantId + " name=" + name + " description=" + description + " active=" + active + "]";
  }
}
