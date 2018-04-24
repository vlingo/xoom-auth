// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.vlingo.auth.model.Permission;
import io.vlingo.auth.model.PermissionRepository;
import io.vlingo.auth.model.TenantId;

public class InMemoryPermissionRepository extends BaseRepository implements PermissionRepository {
  private final Map<String, Permission> permissions = new HashMap<>();

  @Override
  public Permission permissionOf(final TenantId tenantId, final String permissionName) {
    final Permission permission = permissions.get(keyFor(tenantId, permissionName));
    return permission == null ? Permission.NonExisting : permission;
  }

  @Override
  public Collection<Permission> permissionsOf(TenantId tenantId) {
    final Set<Permission> tenantPermissions = new HashSet<>();
    final String tenantKey = keyFor(tenantId);
    for (final String key : permissions.keySet()) {
      if (key.startsWith(tenantKey)) {
        tenantPermissions.add(permissions.get(key));
      }
    }
    return tenantPermissions;
  }

  @Override
  public void save(final Permission permission) {
    permissions.put(keyFor(permission.tenantId(), permission.name()), permission);
  }
}
