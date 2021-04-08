// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infra.persistence;

import io.vlingo.xoom.auth.model.GroupRepository;
import io.vlingo.xoom.auth.model.Loader;
import io.vlingo.xoom.auth.model.PermissionRepository;
import io.vlingo.xoom.auth.model.Properties;
import io.vlingo.xoom.auth.model.RoleRepository;
import io.vlingo.xoom.auth.model.TenantRepository;
import io.vlingo.xoom.auth.model.UserRepository;

public class RepositoryProvider {
  private static GroupRepository groupRepository;
  private static PermissionRepository permissionRepository;
  private static RoleRepository roleRepository;
  private static TenantRepository tenantRepository;
  private static UserRepository userRepository;
  private static Loader loader;

  public static synchronized GroupRepository groupRepository() {
    if (groupRepository == null) {
      groupRepository = instanceOf("repository.group", "io.vlingo.xoom.auth.infra.persistence.InMemoryGroupRepository");
    }
    return groupRepository;
  }

  public static synchronized PermissionRepository permissionRepository() {
    if (permissionRepository == null) {
      permissionRepository = instanceOf("repository.permission", "io.vlingo.xoom.auth.infra.persistence.InMemoryPermissionRepository");
    }
    return permissionRepository;
  }

  public static synchronized RoleRepository roleRepository() {
    if (roleRepository == null) {
      roleRepository = instanceOf("repository.role", "io.vlingo.xoom.auth.infra.persistence.InMemoryRoleRepository");
    }
    return roleRepository;
  }

  public static synchronized TenantRepository tenantRepository() {
    if (tenantRepository == null) {
      tenantRepository = instanceOf("repository.tenant", "io.vlingo.xoom.auth.infra.persistence.InMemoryTenantRepository");
    }
    return tenantRepository;
  }

  public static synchronized UserRepository userRepository() {
    if (userRepository == null) {
      userRepository = instanceOf("repository.user", "io.vlingo.xoom.auth.infra.persistence.InMemoryUserRepository");
    }
    return userRepository;
  }

  public static synchronized Loader loader() {
    if (loader == null) {
      loader = new InMemoryLoader(groupRepository(), permissionRepository(), roleRepository());
    }
    return loader;
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
