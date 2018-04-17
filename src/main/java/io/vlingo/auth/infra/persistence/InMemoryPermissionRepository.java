// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.persistence;

import java.util.HashMap;
import java.util.Map;

import io.vlingo.auth.model.Permission;
import io.vlingo.auth.model.PermissionRepository;
import io.vlingo.auth.model.TenantId;

public class InMemoryPermissionRepository extends BaseRepository implements PermissionRepository {
  private final Map<String, Permission> permissions = new HashMap<>();

  @Override
  public Permission permissionOf(final TenantId tenantId, final String permissionName) {
    return permissions.get(keyFor(tenantId, permissionName));
  }

  @Override
  public void save(final Permission permission) {
    permissions.put(keyFor(permission.tenantId(), permission.name()), permission);
  }
}
