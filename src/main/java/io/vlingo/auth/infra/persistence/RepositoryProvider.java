// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.persistence;

import io.vlingo.auth.model.GroupRepository;
import io.vlingo.auth.model.PermissionRepository;
import io.vlingo.auth.model.Properties;
import io.vlingo.auth.model.RoleRepository;
import io.vlingo.auth.model.TenantRepository;
import io.vlingo.auth.model.UserRepository;

public class RepositoryProvider {
  private static GroupRepository groupRepository;
  private static PermissionRepository permissionRepository;
  private static RoleRepository roleRepository;
  private static TenantRepository tenantRepository;
  private static UserRepository userRepository;

  public static synchronized GroupRepository groupRepository() {
    if (groupRepository == null) {
      groupRepository = instanceOf("repository.group", "io.vlingo.auth.infra.persistence.InMemoryGroupRepository");
    }
    return groupRepository;
  }

  public static synchronized PermissionRepository permissionRepository() {
    if (permissionRepository == null) {
      permissionRepository = instanceOf("repository.permission", "io.vlingo.auth.infra.persistence.InMemoryPermissionRepository");
    }
    return permissionRepository;
  }

  public static synchronized RoleRepository roleRepository() {
    if (roleRepository == null) {
      roleRepository = instanceOf("repository.role", "io.vlingo.auth.infra.persistence.InMemoryRoleRepository");
    }
    return roleRepository;
  }

  public static synchronized TenantRepository tenantRepository() {
    if (tenantRepository == null) {
      tenantRepository = instanceOf("repository.tenant", "io.vlingo.auth.infra.persistence.InMemoryTenantRepository");
    }
    return tenantRepository;
  }

  public static synchronized UserRepository userRepository() {
    if (userRepository == null) {
      userRepository = instanceOf("repository.user", "io.vlingo.auth.infra.persistence.InMemoryUserRepository");
    }
    return userRepository;
  }

  @SuppressWarnings("unchecked")
  private static <T> T instanceOf(final String repositoryProperty, final String defaultClassname) {
    final String classname = Properties.instance.getString(repositoryProperty, defaultClassname);
    try {
      return (T) Class.forName(classname).newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException("Repository type not set or missing: " + repositoryProperty, e);
    }
  }
}
