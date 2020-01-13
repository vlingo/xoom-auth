// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import java.util.HashMap;
import java.util.Map;

public class Repository implements Loader {
  private final Map<String,Group> groups = new HashMap<>();
  private final Map<String,Permission> permissions = new HashMap<>();
  private final Map<String,Role> roles = new HashMap<>();

  @Override
  public Group loadGroup(final TenantId tenantId, final String groupName) {
    return groups.get(keyFor(tenantId, groupName));
  }

  @Override
  public Permission loadPermission(TenantId tenantId, String permissionName) {
    return permissions.get(keyFor(tenantId, permissionName));
  }

  @Override
  public Role loadRole(final TenantId tenantId, final String roleName) {
    return roles.get(keyFor(tenantId, roleName));
  }

  void add(final Group group) {
    groups.put(keyFor(group.tenantId(), group.name()), group);
  }

  void add(final Permission permission) {
    permissions.put(keyFor(permission.tenantId(), permission.name()), permission);
  }

  void add(final Role role) {
    roles.put(keyFor(role.tenantId(), role.name()), role);
  }

  private String keyFor(final TenantId tenantId, final String groupName) {
    return tenantId.value + ":" + groupName;
  }
}
