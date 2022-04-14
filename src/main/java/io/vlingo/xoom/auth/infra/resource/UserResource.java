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
import static io.vlingo.xoom.http.Response.Status.Unauthorized;

import io.vlingo.xoom.auth.infra.persistence.RepositoryProvider;
import io.vlingo.xoom.auth.model.Authenticator;
import io.vlingo.xoom.auth.model.Credential;
import io.vlingo.xoom.auth.model.EmailAddress;
import io.vlingo.xoom.auth.model.Loader;
import io.vlingo.xoom.auth.model.Permission;
import io.vlingo.xoom.auth.model.PermissionRepository;
import io.vlingo.xoom.auth.model.PersonName;
import io.vlingo.xoom.auth.model.Phone;
import io.vlingo.xoom.auth.model.Profile;
import io.vlingo.xoom.auth.model.Role;
import io.vlingo.xoom.auth.model.RoleRepository;
import io.vlingo.xoom.auth.model.TenantId;
import io.vlingo.xoom.auth.model.User;
import io.vlingo.xoom.auth.model.UserRepository;
import io.vlingo.xoom.auth.model.crypto.AuthHasher;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.resource.ResourceHandler;

public class UserResource extends ResourceHandler {
  private final PermissionRepository permissionRepository = RepositoryProvider.permissionRepository();
  private final RoleRepository roleRepository = RepositoryProvider.roleRepository();
  private final UserRepository userRepository = RepositoryProvider.userRepository();
  private final Loader loader = RepositoryProvider.loader();
  private final Authenticator authenticator = new Authenticator(AuthHasher.defaultHasher(), userRepository);

  public void authenticate(final String tenantId, final String username, final String password) {
    final String userToken = authenticator.authenticUserToken(TenantId.fromExisting(tenantId), username, password);
    if (userToken != null) {
      completes().with(Response.of(Created, userToken));
    } else {
      completes().with(Response.of(Unauthorized));
    }
  }

  public void activate(final String tenantId, final String username) {
    final User user = userRepository.userOf(TenantId.fromExisting(tenantId), username);
    if (user.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, username)));
    } else {
      user.activate();
      userRepository.save(user);
      completes().with(Response.of(Ok, serialized(UserData.from(user))));
    }
  }

  public void deactivate(final String tenantId, final String username) {
    final User user = userRepository.userOf(TenantId.fromExisting(tenantId), username);
    if (user.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, username)));
    } else {
      user.deactivate();
      userRepository.save(user);
      completes().with(Response.of(Ok, serialized(UserData.from(user))));
    }
  }

  public void addCredential(final String tenantId, final String username, final CredentialData credentialData) {
    final User user = userRepository.userOf(TenantId.fromExisting(tenantId), username);
    if (user.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, username)));
    } else {
      user.add(Credential.credentialFrom(credentialData.authority, credentialData.id, credentialData.secret, credentialData.type));
      userRepository.save(user);
      completes().with(Response.of(Ok, serialized(UserData.from(user))));
    }
  }

  public void removeCredential(final String tenantId, final String username, final String authority) {
    final User user = userRepository.userOf(TenantId.fromExisting(tenantId), username);
    if (user.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, username)));
    } else {
      final Credential credential = user.credentialOf(authority);
      if (credential != null) {
        user.remove(credential);
        userRepository.save(user);
      }
      completes().with(Response.of(Ok, serialized(UserData.from(user))));
    }
  }

  public void replaceCredential(final String tenantId, final String username, final String authority, final CredentialData credentialData) {
    final User user = userRepository.userOf(TenantId.fromExisting(tenantId), username);
    if (user.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, username)));
    } else {
      final Credential credential = user.credentialOf(authority);
      if (credential != null) {
        user.replace(credential, Credential.credentialFrom(credentialData.authority, credentialData.id, credentialData.secret, credentialData.type));
        userRepository.save(user);
        completes().with(Response.of(Ok, serialized(UserData.from(user))));
      } else {
        completes().with(Response.of(NotFound, location(tenantId, username, authority)));
      }
    }
  }

  public void profile(final String tenantId, final String username, final ProfileData profileData) {
    final User user = userRepository.userOf(TenantId.fromExisting(tenantId), username);
    if (user.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, username)));
    } else {
      final Profile profile = Profile.with(PersonName.of(profileData.name.given, profileData.name.second, profileData.name.family), EmailAddress.of(profileData.emailAddress), Phone.of(profileData.phone));
      user.replace(profile);
      userRepository.save(user);
      completes().with(Response.of(Ok, serialized(UserData.from(user))));
    }
  }

  public void queryUser(final String tenantId, final String username) {
    final User user = userRepository.userOf(TenantId.fromExisting(tenantId), username);
    if (user.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, username)));
    } else {
      completes().with(Response.of(Ok, serialized(UserData.from(user))));
    }
  }

  public void queryPermission(final String tenantId, final String username, final String permissionName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final User user = userRepository.userOf(parentTenantId, username);
    if (user.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, username)));
    } else {
      final Permission permission = permissionRepository.permissionOf(parentTenantId, permissionName);
      if (permission.doesNotExist()) {
        completes().with(Response.of(NotFound, "Permission does not exist: " + permissionName));
      } else {
        if (!user.hasPermission(permission, loader)) {
          completes().with(Response.of(NotFound, "User " + username + " does not have permission: " + permissionName));
        } else {
          completes().with(Response.of(Ok, serialized(PermissionData.from(permission))));
        }
      }
    }
  }

  public void queryRole(final String tenantId, final String username, final String roleName) {
    final TenantId parentTenantId = TenantId.fromExisting(tenantId);
    final User user = userRepository.userOf(parentTenantId, username);
    if (user.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId, username)));
    } else {
      final Role role = roleRepository.roleOf(parentTenantId, roleName);
      if (role.doesNotExist()) {
        completes().with(Response.of(NotFound, "Role does not exist: " + roleName));
      } else {
        if (!user.isInRole(role, loader)) {
          completes().with(Response.of(NotFound, "User " + username + " is not in role: " + roleName));
        } else {
          completes().with(Response.of(Ok, serialized(RoleData.from(role))));
        }
      }
    }
  }

  private String location(final String tenantId, final String username) {
    return "/tenants/" + tenantId + "/users/" + username;
  }

  private String location(final String tenantId, final String username, final String authority) {
    return "/tenants/" + tenantId + "/users/" + username + "/credentials/" + authority;
  }
}
