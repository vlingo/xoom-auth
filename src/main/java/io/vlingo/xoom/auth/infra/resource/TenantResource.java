// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infra.resource;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.Created;
import static io.vlingo.xoom.http.Response.Status.NotFound;
import static io.vlingo.xoom.http.Response.Status.Ok;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static io.vlingo.xoom.http.ResponseHeader.headers;
import static io.vlingo.xoom.http.ResponseHeader.of;

import java.util.Collection;

import io.vlingo.xoom.auth.infra.persistence.RepositoryProvider;
import io.vlingo.xoom.auth.model.Credential;
import io.vlingo.xoom.auth.model.EmailAddress;
import io.vlingo.xoom.auth.model.Group;
import io.vlingo.xoom.auth.model.GroupRepository;
import io.vlingo.xoom.auth.model.Permission;
import io.vlingo.xoom.auth.model.PermissionRepository;
import io.vlingo.xoom.auth.model.PersonName;
import io.vlingo.xoom.auth.model.Phone;
import io.vlingo.xoom.auth.model.Profile;
import io.vlingo.xoom.auth.model.Role;
import io.vlingo.xoom.auth.model.RoleRepository;
import io.vlingo.xoom.auth.model.Tenant;
import io.vlingo.xoom.auth.model.TenantId;
import io.vlingo.xoom.auth.model.TenantRepository;
import io.vlingo.xoom.auth.model.User;
import io.vlingo.xoom.auth.model.UserRepository;
import io.vlingo.xoom.auth.model.crypto.AuthHasher;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.resource.ResourceHandler;

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
    System.out.println("registerUser 1");
    if (tenant.doesNotExist()) {
      System.out.println("registerUser failed");
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      System.out.println("registerUser profile");
      final Profile profile = Profile.with(PersonName.of(userData.profile.name.given, userData.profile.name.second, userData.profile.name.family), EmailAddress.of(userData.profile.emailAddress), Phone.of(userData.profile.phone));
      System.out.println("registerUser crypto");
      final String cryptoSecret = AuthHasher.defaultHasher().hash(userData.credential.secret);
      System.out.println("registerUser credential");
      final Credential credential = Credential.vlingoCredentialFrom(userData.credential.authority, userData.credential.id, cryptoSecret);
      System.out.println("registerUser register");
      final User user = tenant.registerUser(userData.username, profile, credential, userData.active);
      System.out.println("registerUser save");
      userRepository.save(user);
      completes().with(Response.of(Created, headers(of(Location, location(tenant.tenantId().value, "users", user.username()))), serialized(UserRegistrationData.from(user))));
    }
  }

  public void queryTenants() {
    final Collection<Tenant> tenants = tenantRepository.allTenants();
    if (tenants.isEmpty()) {
      completes().with(Response.of(Ok));
    } else {
      completes().with(Response.of(Ok, serialized(TenantData.from(tenants))));
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
