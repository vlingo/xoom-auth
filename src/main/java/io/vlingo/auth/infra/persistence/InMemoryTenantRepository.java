// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.persistence;

import java.util.HashMap;
import java.util.Map;

import io.vlingo.auth.model.Tenant;
import io.vlingo.auth.model.TenantId;
import io.vlingo.auth.model.TenantRepository;

public class InMemoryTenantRepository extends BaseRepository implements TenantRepository {
  private final Map<String, Tenant> tenants = new HashMap<>();

  @Override
  public Tenant tenantOf(final TenantId tenantId) {
    final Tenant tenant = tenants.get(tenantId.value);
    
    return tenant == null ? Tenant.NonExisting : tenant;
  }

  @Override
  public void save(final Tenant tenant) {
    tenants.put(tenant.tenantId().value, tenant);
  }
}
