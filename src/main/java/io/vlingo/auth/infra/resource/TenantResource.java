// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Created;
import static io.vlingo.http.Response.NotFound;
import static io.vlingo.http.Response.Ok;
import static io.vlingo.http.ResponseHeader.Location;
import static io.vlingo.http.ResponseHeader.headers;
import static io.vlingo.http.ResponseHeader.of;

import java.util.Collection;

import io.vlingo.auth.infra.persistence.RepositoryProvider;
import io.vlingo.auth.model.Credential;
import io.vlingo.auth.model.EmailAddress;
import io.vlingo.auth.model.Group;
import io.vlingo.auth.model.GroupRepository;
import io.vlingo.auth.model.Permission;
import io.vlingo.auth.model.PermissionRepository;
import io.vlingo.auth.model.PersonName;
import io.vlingo.auth.model.Phone;
import io.vlingo.auth.model.Profile;
import io.vlingo.auth.model.Role;
import io.vlingo.auth.model.RoleRepository;
import io.vlingo.auth.model.Tenant;
import io.vlingo.auth.model.TenantId;
import io.vlingo.auth.model.TenantRepository;
import io.vlingo.auth.model.User;
import io.vlingo.auth.model.UserRepository;
import io.vlingo.auth.model.crypto.Hasher;
import io.vlingo.http.Response;
import io.vlingo.http.resource.ResourceHandler;

public class TenantResource extends ResourceHandler {
  private final GroupRepository groupRepository = RepositoryProvider.groupRepository();
  private final PermissionRepository permissionRepository = RepositoryProvider.permissionRepository();
  private final RoleRepository roleRepository = RepositoryProvider.roleRepository();
  private final TenantRepository tenantRepository = RepositoryProvider.tenantRepository();
  private final UserRepository userRepository = RepositoryProvider.userRepository();

  public TenantResource() { }

  public void subscribeFor(final TenantData tenantData) {
    final Tenant tenant = Tenant.subscribeFor(tenantData.name, tenantData.description, tenantData.active);

    tenantRepository.save(tenant);

    completes().with(Response.of(Created, headers(of(Location, location(tenant.tenantId().value))), serialized(TenantData.from(tenant))));
  }

  public void activate(final String tenantId) {
    final Tenant tenant = tenantRepository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      tenant.activate();
      tenantRepository.save(tenant);
      completes().with(Response.of(Ok, serialized(TenantData.from(tenant))));
    }
  }

  public void deactivate(final String tenantId) {
    final Tenant tenant = tenantRepository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      tenant.deactivate();
      tenantRepository.save(tenant);
      completes().with(Response.of(Ok, serialized(TenantData.from(tenant))));
    }
  }

  public void changeDescription(final String tenantId, final String description) {
    final Tenant tenant = tenantRepository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      tenant.changeDescription(description);
      tenantRepository.save(tenant);
      completes().with(Response.of(Ok, serialized(TenantData.from(tenant))));
    }
  }

  public void changeName(final String tenantId, final String name) {
    final Tenant tenant = tenantRepository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      tenant.changeName(name);
      tenantRepository.save(tenant);
      completes().with(Response.of(Ok, serialized(TenantData.from(tenant))));
    }
  }

  public void provisionGroup(final String tenantId, final GroupData groupData) {
    final Tenant tenant = tenantRepository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      final Group group = tenant.provisionGroup(groupData.name, groupData.description);
      groupRepository.save(group);
      completes().with(Response.of(Created, headers(of(Location, location(tenant.tenantId().value, "groups", group.name()))), serialized(GroupData.from(group))));
    }
  }

  public void provisionPermission(final String tenantId, final PermissionData permissionData) {
    final Tenant tenant = tenantRepository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      final Permission permission = tenant.provisionPermission(permissionData.name, permissionData.description);
      permissionRepository.save(permission);
      completes().with(Response.of(Created, headers(of(Location, location(tenant.tenantId().value, "permissions", permission.name()))), serialized(PermissionData.from(permission))));
    }
  }

  public void provisionRole(final String tenantId, final RoleData roleData) {
    final Tenant tenant = tenantRepository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      final Role role = tenant.provisionRole(roleData.name, roleData.description);
      roleRepository.save(role);
      completes().with(Response.of(Created, headers(of(Location, location(tenant.tenantId().value, "roles", role.name()))), serialized(RoleData.from(role))));
    }
  }

  public void registerUser(final String tenantId, final UserRegistrationData userData) {
    final Tenant tenant = tenantRepository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      final Profile profile = Profile.with(PersonName.of(userData.profile.name.given, userData.profile.name.second, userData.profile.name.family), EmailAddress.of(userData.profile.emailAddress), Phone.of(userData.profile.phone));
      final String cryptoSecret = Hasher.defaultHasher().hash(userData.credential.secret);
      final Credential credential = Credential.vlingoCredentialFrom(userData.credential.authority, userData.credential.id, cryptoSecret);
      final User user = tenant.registerUser(userData.username, profile, credential, userData.active);
      userRepository.save(user);
      completes().with(Response.of(Created, headers(of(Location, location(tenant.tenantId().value, "users", user.username()))), serialized(UserRegistrationData.from(user))));
    }
  }

  public void queryTenant(final String tenantId) {
    final Tenant tenant = tenantRepository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      completes().with(Response.of(Ok, serialized(TenantData.from(tenant))));
    }
  }

  public void queryGroups(final String tenantId) {
    final Collection<Group> groups = groupRepository.groupsOf(TenantId.fromExisting(tenantId));
    completes().with(Response.of(Ok, serialized(GroupData.from(groups))));
  }

  public void queryPermissions(final String tenantId) {
    final Collection<Permission> permissions = permissionRepository.permissionsOf(TenantId.fromExisting(tenantId));
    completes().with(Response.of(Ok, serialized(PermissionData.from(permissions))));
  }

  public void queryRoles(final String tenantId) {
    final Collection<Role> roles = roleRepository.rolesOf(TenantId.fromExisting(tenantId));
    completes().with(Response.of(Ok, serialized(RoleData.from(roles))));
  }

  public void queryUsers(final String tenantId) {
    final Collection<User> users = userRepository.usersOf(TenantId.fromExisting(tenantId));
    completes().with(Response.of(Ok, serialized(MinimalUserData.from(users))));
  }

  private String location(final String tenantId) {
    return "/tenants/" + tenantId;
  }

  private String location(final String tenantId, final String childType, final String childId) {
    return "/tenants/" + tenantId + "/" + childType + "/" + childId;
  }
}
