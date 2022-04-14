// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infra.resource;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.NotFound;
import static io.vlingo.xoom.http.Response.Status.Ok;

import io.vlingo.xoom.auth.infra.persistence.RepositoryProvider;
import io.vlingo.xoom.auth.model.Group;
import io.vlingo.xoom.auth.model.GroupRepository;
import io.vlingo.xoom.auth.model.Loader;
import io.vlingo.xoom.auth.model.Permission;
import io.vlingo.xoom.auth.model.PermissionRepository;
import io.vlingo.xoom.auth.model.Role;
import io.vlingo.xoom.auth.model.RoleRepository;
import io.vlingo.xoom.auth.model.TenantId;
import io.vlingo.xoom.auth.model.User;
import io.vlingo.xoom.auth.model.UserRepository;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.resource.ResourceHandler;

public class RoleResource extends ResourceHandler {
  private final GroupRepository groupRepository = RepositoryProvider.groupRepository();
  private final PermissionRepository permissionRepository = RepositoryProvider.permissionRepository();
  private final RoleRepository roleRepository = RepositoryProvider.roleRepository();
  private final UserRepository userRepository = RepositoryProvider.userRepository();
  private final Loader loader = RepositoryProvider.loader();

  public RoleResource() { }

  public void changeDescription(final String tenantId, final String roleName, final String description) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Role role = roleRepository.roleOf(parentTenantId, roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      role.changeDescription(description);
      roleRepository.save(role);
      completes().with(Response.of(Ok, serialized(RoleData.from(role))));
    }
  }

  public void assignGroup(final String tenantId, final String roleName, final String groupName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Role role = roleRepository.roleOf(parentTenantId, roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      final Group group = groupRepository.groupOf(parentTenantId, groupName);
      if (group.doesNotExist()) {
        completes().with(Response.of(NotFound, "Group does not exist: " + groupName));
      } else {
        role.assign(group);
        // TODO: transaction
        roleRepository.save(role);
        groupRepository.save(group);
        completes().with(Response.of(Ok, location(tenantId, roleName, "groups", groupName)));
      }
    }
  }

  public void unassignGroup(final String tenantId, final String roleName, final String groupName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Role role = roleRepository.roleOf(parentTenantId, roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      final Group group = groupRepository.groupOf(parentTenantId, groupName);
      if (group.doesNotExist()) {
        completes().with(Response.of(NotFound, "Group does not exist: " + groupName));
      } else {
        role.unassign(group);
        // TODO: transaction
        roleRepository.save(role);
        groupRepository.save(group);
        completes().with(Response.of(Ok));
      }
    }
  }

  public void assignUser(final String tenantId, final String roleName, final String username) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Role role = roleRepository.roleOf(parentTenantId, roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      final User user = userRepository.userOf(parentTenantId, username);
      if (user.doesNotExist()) {
        completes().with(Response.of(NotFound, "User does not exist: " + username));
      } else {
        role.assign(user);
        // TODO: transaction
        roleRepository.save(role);
        userRepository.save(user);
        completes().with(Response.of(Ok, location(tenantId, roleName, "users", username)));
      }
    }
  }

  public void unassignUser(final String tenantId, final String roleName, final String username) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Role role = roleRepository.roleOf(parentTenantId, roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      final User user = userRepository.userOf(parentTenantId, username);
      if (user.doesNotExist()) {
        completes().with(Response.of(NotFound, "User does not exist: " + username));
      } else {
        role.unassign(user);
        // TODO: transaction
        roleRepository.save(role);
        userRepository.save(user);
        completes().with(Response.of(Ok));
      }
    }
  }

  public void attach(final String tenantId, final String roleName, final String permissionName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Role role = roleRepository.roleOf(parentTenantId, roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      final Permission permission = permissionRepository.permissionOf(parentTenantId, permissionName);
      if (permission.doesNotExist()) {
        completes().with(Response.of(NotFound, "Permission does not exist: " + permissionName));
      } else {
        role.attach(permission);
        // TODO: transaction
        roleRepository.save(role);
        permissionRepository.save(permission);
        completes().with(Response.of(Ok, location(tenantId, roleName, "permissions", permissionName)));
      }
    }
  }

  public void detach(final String tenantId, final String roleName, final String permissionName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Role role = roleRepository.roleOf(parentTenantId, roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      final Permission permission = permissionRepository.permissionOf(parentTenantId, permissionName);
      if (permission.doesNotExist()) {
        completes().with(Response.of(NotFound, "Permission does not exist: " + permissionName));
      } else {
        role.detach(permission);
        // TODO: transaction
        roleRepository.save(role);
        permissionRepository.save(permission);
        completes().with(Response.of(Ok, location(tenantId, roleName, "permissions", permissionName)));
      }
    }
  }

  public void queryRole(final String tenantId, final String roleName) {
    final Role role = roleRepository.roleOf(TenantId.fromExisting(tenantId), roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      completes().with(Response.of(Ok, serialized(RoleData.from(role))));
    }
  }

  public void queryPermission(final String tenantId, final String roleName, final String permissionName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Role role = roleRepository.roleOf(parentTenantId, roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      if (role.hasPermissionOf(permissionName)) {
        final Permission permission = permissionRepository.permissionOf(parentTenantId, permissionName);
        if (permission.doesNotExist()) {
          completes().with(Response.of(NotFound, "Role has permission but permission does not exist: " + permissionName));
        } else {
          completes().with(Response.of(Ok, serialized(PermissionData.from(permission))));
        }
      } else {
        completes().with(Response.of(NotFound, "Role does not have permission: " + permissionName));
      }
    }    
  }

  public void queryGroup(final String tenantId, final String roleName, final String groupName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Role role = roleRepository.roleOf(parentTenantId, roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      final Group group = groupRepository.groupOf(parentTenantId, groupName);
      if (group.doesNotExist()) {
        completes().with(Response.of(NotFound, "Group does not exist: " + groupName));
      } else {
        if (!role.isInRole(group, loader)) {
          completes().with(Response.of(NotFound, "Group is not in role: " + groupName));
        } else {
          completes().with(Response.of(Ok, serialized(GroupData.from(group))));
        }
      }
    }
  }

  public void queryUser(final String tenantId, final String roleName, final String username) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Role role = roleRepository.roleOf(parentTenantId, roleName);
    if (role.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, roleName)));
    } else {
      final User user = userRepository.userOf(parentTenantId, username);
      if (user.doesNotExist() || !user.isActive()) {
        completes().with(Response.of(NotFound, "User does not exist or is not active: " + username));
      } else {
        if (!role.isInRole(user, loader)) {
          completes().with(Response.of(NotFound, "User is not in role: " + username));
        } else {
          completes().with(Response.of(Ok, serialized(MinimalUserData.from(user))));
        }
      }
    }
  }

  private String location(final String tenantId, final String roleName) {
    return "/tenants/" + tenantId + "/roles/" + roleName;
  }

  private String location(final String tenantId, final String roleName, final String childType, final String childId) {
    return location(tenantId, roleName) + "/" + childType + "/" + childId;
  }
}
