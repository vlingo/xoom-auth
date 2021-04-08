// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
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

public class GroupResource extends ResourceHandler {
  private final GroupRepository groupRepository = RepositoryProvider.groupRepository();
  private final PermissionRepository permissionRepository = RepositoryProvider.permissionRepository();
  private final RoleRepository roleRepository = RepositoryProvider.roleRepository();
  private final UserRepository userRepository = RepositoryProvider.userRepository();
  private final Loader loader = RepositoryProvider.loader();

  public GroupResource() { }

  public void changeDescription(final String tenantId, final String groupName, final String description) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Group group = groupRepository.groupOf(parentTenantId, groupName);
    if (group.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, groupName)));
    } else {
      group.changeDescription(description);
      groupRepository.save(group);
      completes().with(Response.of(Ok, serialized(GroupData.from(group))));
    }
  }

  public void assignGroup(final String tenantId, final String groupName, final String innerGroupName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Group group = groupRepository.groupOf(parentTenantId, groupName);
    if (group.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, groupName)));
    } else {
      final Group innerGroup = groupRepository.groupOf(parentTenantId, innerGroupName);
      if (innerGroup.doesNotExist()) {
        completes().with(Response.of(NotFound, "Group does not exist: " + innerGroupName));
      } else {
        group.assign(innerGroup);
        // TODO: transaction
        groupRepository.save(group);
        groupRepository.save(innerGroup);
        completes().with(Response.of(Ok, location(tenantId, groupName, "groups", innerGroupName)));
      }
    }
  }

  public void unassignGroup(final String tenantId, final String groupName, final String innerGroupName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Group group = groupRepository.groupOf(parentTenantId, groupName);
    if (group.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, groupName)));
    } else {
      final Group innerGroup = groupRepository.groupOf(parentTenantId, innerGroupName);
      if (innerGroup.doesNotExist()) {
        completes().with(Response.of(NotFound, "Group does not exist: " + innerGroupName));
      } else {
        group.unassign(innerGroup);
        // TODO: transaction
        groupRepository.save(group);
        groupRepository.save(innerGroup);
        completes().with(Response.of(Ok));
      }
    }
  }

  public void assignUser(final String tenantId, final String groupName, final String username) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Group group = groupRepository.groupOf(parentTenantId, groupName);
    if (group.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, groupName)));
    } else {
      final User user = userRepository.userOf(parentTenantId, username);
      if (user.doesNotExist()) {
        completes().with(Response.of(NotFound, "User does not exist: " + username));
      } else {
        group.assign(user);
        // TODO: transaction
        groupRepository.save(group);
        userRepository.save(user);
        completes().with(Response.of(Ok, location(tenantId, groupName, "users", username)));
      }
    }
  }

  public void unassignUser(final String tenantId, final String groupName, final String username) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Group group = groupRepository.groupOf(parentTenantId, groupName);
    if (group.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, groupName)));
    } else {
      final User user = userRepository.userOf(parentTenantId, username);
      if (user.doesNotExist()) {
        completes().with(Response.of(NotFound, "User does not exist: " + username));
      } else {
        group.unassign(user);
        // TODO: transaction
        groupRepository.save(group);
        userRepository.save(user);
        completes().with(Response.of(Ok));
      }
    }
  }

  public void queryGroup(final String tenantId, final String groupName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Group group = groupRepository.groupOf(parentTenantId, groupName);
    if (group.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, groupName)));
    } else {
      completes().with(Response.of(Ok, serialized(GroupData.from(group))));
    }
  }

  public void queryInnerGroup(final String tenantId, final String groupName, final String innerGroupName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Group group = groupRepository.groupOf(parentTenantId, groupName);
    if (group.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, groupName)));
    } else {
      final Group innerGroup = groupRepository.groupOf(parentTenantId, innerGroupName);
      if (innerGroup.doesNotExist()) {
        completes().with(Response.of(NotFound, "Group does not exist: " + innerGroupName));
      } else {
        if (!group.hasMember(innerGroup, loader)) {
          completes().with(Response.of(NotFound, "Group " + groupName + " does not have member group: " + innerGroupName));
        } else {
          completes().with(Response.of(Ok, serialized(GroupData.from(innerGroup))));
        }
      }
    }
  }

  public void queryPermission(final String tenantId, final String groupName, final String permissionName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Group group = groupRepository.groupOf(parentTenantId, groupName);
    if (group.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, groupName)));
    } else {
      final Permission permission = permissionRepository.permissionOf(parentTenantId, permissionName);
      if (permission.doesNotExist()) {
        completes().with(Response.of(NotFound, "Permission does not exist: " + permissionName));
      } else {
        if (!group.hasPermission(permission, loader)) {
          completes().with(Response.of(NotFound, "Group " + groupName + " does not have member permission: " + permissionName));
        } else {
          completes().with(Response.of(Ok, serialized(PermissionData.from(permission))));
        }
      }
    }
  }

  public void queryRole(final String tenantId, final String groupName, final String roleName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Group group = groupRepository.groupOf(parentTenantId, groupName);
    if (group.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, groupName)));
    } else {
      final Role role = roleRepository.roleOf(parentTenantId, roleName);
      if (role.doesNotExist()) {
        completes().with(Response.of(NotFound, "Role does not exist: " + roleName));
      } else {
        if (!group.isInRole(role, loader)) {
          completes().with(Response.of(NotFound, "Group " + groupName + " is not in role: " + roleName));
        } else {
          completes().with(Response.of(Ok, serialized(RoleData.from(role))));
        }
      }
    }
  }

  public void queryUser(final String tenantId, final String groupName, final String username) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final Group group = groupRepository.groupOf(parentTenantId, groupName);
    if (group.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, groupName)));
    } else {
      final User user = userRepository.userOf(parentTenantId, username);
      if (user.doesNotExist() || !user.isActive()) {
        completes().with(Response.of(NotFound, "User does not exist or is not active: " + username));
      } else {
        if (!group.hasMember(user, loader)) {
          completes().with(Response.of(NotFound, "Group " + groupName + " does not have member user: " + username));
        } else {
          completes().with(Response.of(Ok, serialized(MinimalUserData.from(user))));
        }
      }
    }
  }

  private String location(final String tenantId, final String roleName) {
    return "/tenants/" + tenantId + "/groups/" + roleName;
  }

  private String location(final String tenantId, final String roleName, final String childType, final String childId) {
    return location(tenantId, roleName) + "/" + childType + "/" + childId;
  }
}
