// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infra.persistence;

import io.vlingo.xoom.auth.model.Group;
import io.vlingo.xoom.auth.model.GroupRepository;
import io.vlingo.xoom.auth.model.Loader;
import io.vlingo.xoom.auth.model.Permission;
import io.vlingo.xoom.auth.model.PermissionRepository;
import io.vlingo.xoom.auth.model.Role;
import io.vlingo.xoom.auth.model.RoleRepository;
import io.vlingo.xoom.auth.model.TenantId;

public class InMemoryLoader implements Loader {
  private final GroupRepository groupRepository;
  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;

  public InMemoryLoader(
          final GroupRepository groupRepository,
          final PermissionRepository permissionRepository,
          final RoleRepository roleRepository) {
    this.groupRepository = groupRepository;
    this.permissionRepository = permissionRepository;
    this.roleRepository = roleRepository;
  }

  @Override
  public Group loadGroup(final TenantId tenantId, final String groupName) {
    return groupRepository.groupOf(tenantId, groupName);
  }

  @Override
  public Permission loadPermission(final TenantId tenantId, final String permissionName) {
    return permissionRepository.permissionOf(tenantId, permissionName);
  }

  @Override
  public Role loadRole(final TenantId tenantId, final String roleName) {
    return roleRepository.roleOf(tenantId, roleName);
  }
}
