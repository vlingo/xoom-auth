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

import io.vlingo.auth.model.Role;
import io.vlingo.auth.model.RoleRepository;
import io.vlingo.auth.model.TenantId;

public class InMemoryRoleRepository extends BaseRepository implements RoleRepository {
  private final Map<String, Role> roles = new HashMap<>();

  @Override
  public Role roleOf(final TenantId tenantId, final String roleName) {
    final Role role = roles.get(keyFor(tenantId, roleName));
    return role == null ? Role.NonExisting : role;
  }

  @Override
  public Collection<Role> rolesOf(TenantId tenantId) {
    final Set<Role> tenantRoles = new HashSet<>();
    final String tenantKey = keyFor(tenantId);
    for (final String key : roles.keySet()) {
      if (key.startsWith(tenantKey)) {
        tenantRoles.add(roles.get(key));
      }
    }
    return tenantRoles;
  }

  @Override
  public void save(final Role role) {
    roles.put(keyFor(role.tenantId(), role.name()), role);
  }
}
