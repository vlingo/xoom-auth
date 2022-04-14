// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infra.resource;

import java.util.Properties;

public class TestProperties {

  public static Properties groupResourceProperties(final Properties properties) {
    
    properties.setProperty("resource.name.group", "[description, assignGroup, unassignGroup, assignUser, unassignUser, queryGroup, queryInnerGroup, queryPermission, queryRole, queryUser]");

    properties.setProperty("resource.group.handler", "io.vlingo.xoom.auth.infra.resource.GroupResource");
    properties.setProperty("resource.group.pool", "10");
    properties.setProperty("resource.group.disallowPathParametersWithSlash", "true");

    properties.setProperty("action.group.description.method", "PATCH");
    properties.setProperty("action.group.description.uri", "/tenants/{tenantId}/groups/{groupName}/description");
    properties.setProperty("action.group.description.to", "changeDescription(String tenantId, String groupName, body:java.lang.String description)");
    properties.setProperty("action.group.description.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.group.assignGroup.method", "PUT");
    properties.setProperty("action.group.assignGroup.uri", "/tenants/{tenantId}/groups/{groupName}/groups");
    properties.setProperty("action.group.assignGroup.to", "assignGroup(String tenantId, String groupName, body:java.lang.String groupName)");
    properties.setProperty("action.group.assignGroup.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.group.unassignGroup.method", "DELETE");
    properties.setProperty("action.group.unassignGroup.uri", "/tenants/{tenantId}/groups/{groupName}/groups/{innerGroupName}");
    properties.setProperty("action.group.unassignGroup.to", "unassignGroup(String tenantId, String groupName, String innerGroupName)");
    properties.setProperty("action.group.unassignGroup.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.group.assignUser.method", "PUT");
    properties.setProperty("action.group.assignUser.uri", "/tenants/{tenantId}/groups/{groupName}/users");
    properties.setProperty("action.group.assignUser.to", "assignUser(String tenantId, String groupName, body:java.lang.String username)");
    properties.setProperty("action.group.assignUser.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.group.unassignUser.method", "DELETE");
    properties.setProperty("action.group.unassignUser.uri", "/tenants/{tenantId}/groups/{groupName}/users/{username}");
    properties.setProperty("action.group.unassignUser.to", "unassignUser(String tenantId, String groupName, String username)");
    properties.setProperty("action.group.unassignUser.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.group.queryGroup.method", "GET");
    properties.setProperty("action.group.queryGroup.uri", "/tenants/{tenantId}/groups/{groupName}");
    properties.setProperty("action.group.queryGroup.to", "queryGroup(String tenantId, String groupName)");
    properties.setProperty("action.group.queryGroup.permission", "io.vlingo.xoom.auth.GroupQuery");

    properties.setProperty("action.group.queryInnerGroup.method", "GET");
    properties.setProperty("action.group.queryInnerGroup.uri", "/tenants/{tenantId}/groups/{groupName}/groups/{innerGroupName}");
    properties.setProperty("action.group.queryInnerGroup.to", "queryInnerGroup(String tenantId, String groupName, String innerGroupName)");
    properties.setProperty("action.group.queryInnerGroup.permission", "io.vlingo.xoom.auth.GroupQuery");

    properties.setProperty("action.group.queryPermission.method", "GET");
    properties.setProperty("action.group.queryPermission.uri", "/tenants/{tenantId}/groups/{groupName}/permissions/{permissionName}");
    properties.setProperty("action.group.queryPermission.to", "queryPermission(String tenantId, String groupName, String permissionName)");
    properties.setProperty("action.group.queryPermission.permission", "io.vlingo.xoom.auth.GroupQuery");

    properties.setProperty("action.group.queryRole.method", "GET");
    properties.setProperty("action.group.queryRole.uri", "/tenants/{tenantId}/groups/{groupName}/roles/{roleName}");
    properties.setProperty("action.group.queryRole.to", "queryRole(String tenantId, String groupName, String roleName)");
    properties.setProperty("action.group.queryRole.permission", "io.vlingo.xoom.auth.GroupQuery");

    properties.setProperty("action.group.queryUser.method", "GET");
    properties.setProperty("action.group.queryUser.uri", "/tenants/{tenantId}/groups/{groupName}/users/{username}");
    properties.setProperty("action.group.queryUser.to", "queryUser(String tenantId, String groupName, String username)");
    properties.setProperty("action.group.queryUser.permission", "io.vlingo.xoom.auth.GroupQuery");

    return properties;
  }

  public static Properties permissionResourceProperties(final Properties properties) {
    properties.setProperty("resource.name.permission", "[enforce, enforceReplacement, forget, description, queryPermission]");

    properties.setProperty("resource.permission.handler", "io.vlingo.xoom.auth.infra.resource.PermissionResource");
    properties.setProperty("resource.permission.pool", "5");
    properties.setProperty("resource.permission.disallowPathParametersWithSlash", "true");

    properties.setProperty("action.permission.enforce.method", "PATCH");
    properties.setProperty("action.permission.enforce.uri", "/tenants/{tenantId}/permissions/{permissionName}/constraints");
    properties.setProperty("action.permission.enforce.to", "enforce(String tenantId, String permissionName, body:io.vlingo.xoom.auth.infra.resource.ConstraintData constraintData)");
    properties.setProperty("action.permission.enforce.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.permission.enforceReplacement.method", "PATCH");
    properties.setProperty("action.permission.enforceReplacement.uri", "/tenants/{tenantId}/permissions/{permissionName}/constraints/{constraintName}");
    properties.setProperty("action.permission.enforceReplacement.to", "enforceReplacement(String tenantId, String permissionName, String constraintName, body:io.vlingo.xoom.auth.infra.resource.ConstraintData constraintData)");
    properties.setProperty("action.permission.enforceReplacement.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.permission.forget.method", "DELETE");
    properties.setProperty("action.permission.forget.uri", "/tenants/{tenantId}/permissions/{permissionName}/constraints/{constraintName}");
    properties.setProperty("action.permission.forget.to", "forget(String tenantId, String permissionName, String constraintName)");
    properties.setProperty("action.permission.forget.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.permission.description.method", "PATCH");
    properties.setProperty("action.permission.description.uri", "/tenants/{tenantId}/permissions/{permissionName}/description");
    properties.setProperty("action.permission.description.to", "changeDescription(String tenantId, String permissionName, body:java.lang.String description)");
    properties.setProperty("action.permission.description.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.permission.queryPermission.method", "GET");
    properties.setProperty("action.permission.queryPermission.uri", "/tenants/{tenantId}/permissions/{permissionName}");
    properties.setProperty("action.permission.queryPermission.to", "queryPermission(String tenantId, String permissionName)");
    properties.setProperty("action.permission.queryPermission.permission", "io.vlingo.xoom.auth.Administrator");

    return properties;
  }

  public static Properties roleResourceProperties(final Properties properties) {
    properties.setProperty("resource.name.role", "[description, assignGroup, unassignGroup, assignUser, unassignUser, attach, detach, queryRole, queryPermission, queryGroup, queryUser]");

    properties.setProperty("resource.role.handler", "io.vlingo.xoom.auth.infra.resource.RoleResource");
    properties.setProperty("resource.role.pool", "10");
    properties.setProperty("resource.role.disallowPathParametersWithSlash", "true");

    properties.setProperty("action.role.description.method", "PATCH");
    properties.setProperty("action.role.description.uri", "/tenants/{tenantId}/roles/{roleName}/description");
    properties.setProperty("action.role.description.to", "changeDescription(String tenantId, String roleName, body:java.lang.String description)");
    properties.setProperty("action.role.description.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.role.assignGroup.method", "PUT");
    properties.setProperty("action.role.assignGroup.uri", "/tenants/{tenantId}/roles/{roleName}/groups");
    properties.setProperty("action.role.assignGroup.to", "assignGroup(String tenantId, String roleName, body:java.lang.String groupName)");
    properties.setProperty("action.role.assignGroup.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.role.unassignGroup.method", "DELETE");
    properties.setProperty("action.role.unassignGroup.uri", "/tenants/{tenantId}/roles/{roleName}/groups/{groupName}");
    properties.setProperty("action.role.unassignGroup.to", "unassignGroup(String tenantId, String roleName, String groupName)");
    properties.setProperty("action.role.unassignGroup.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.role.assignUser.method", "PUT");
    properties.setProperty("action.role.assignUser.uri", "/tenants/{tenantId}/roles/{roleName}/users");
    properties.setProperty("action.role.assignUser.to", "assignUser(String tenantId, String roleName, body:java.lang.String username)");
    properties.setProperty("action.role.assignUser.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.role.unassignUser.method", "DELETE");
    properties.setProperty("action.role.unassignUser.uri", "/tenants/{tenantId}/roles/{roleName}/users/{username}");
    properties.setProperty("action.role.unassignUser.to", "unassignUser(String tenantId, String roleName, String username)");
    properties.setProperty("action.role.unassignUser.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.role.attach.method", "PUT");
    properties.setProperty("action.role.attach.uri", "/tenants/{tenantId}/roles/{roleName}/permissions");
    properties.setProperty("action.role.attach.to", "attach(String tenantId, String roleName, body:java.lang.String permissionName)");
    properties.setProperty("action.role.attach.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.role.detach.method", "DELETE");
    properties.setProperty("action.role.detach.uri", "/tenants/{tenantId}/roles/{roleName}/permissions/{permissionName}");
    properties.setProperty("action.role.detach.to", "detach(String tenantId, String roleName, String permissionName)");
    properties.setProperty("action.role.detach.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.role.queryRole.method", "GET");
    properties.setProperty("action.role.queryRole.uri", "/tenants/{tenantId}/roles/{roleName}");
    properties.setProperty("action.role.queryRole.to", "queryRole(String tenantId, String roleName)");
    properties.setProperty("action.role.queryRole.permission", "io.vlingo.xoom.auth.RoleQuery");

    properties.setProperty("action.role.queryPermission.method", "GET");
    properties.setProperty("action.role.queryPermission.uri", "/tenants/{tenantId}/roles/{roleName}/permissions/{permissionName}");
    properties.setProperty("action.role.queryPermission.to", "queryPermission(String tenantId, String roleName, String permissionName)");
    properties.setProperty("action.role.queryPermission.permission", "io.vlingo.xoom.auth.RoleQuery");

    properties.setProperty("action.role.queryGroup.method", "GET");
    properties.setProperty("action.role.queryGroup.uri", "/tenants/{tenantId}/roles/{roleName}/groups/{groupName}");
    properties.setProperty("action.role.queryGroup.to", "queryGroup(String tenantId, String roleName, String groupName)");
    properties.setProperty("action.role.queryGroup.permission", "io.vlingo.xoom.auth.RoleQuery");

    properties.setProperty("action.role.queryUser.method", "GET");
    properties.setProperty("action.role.queryUser.uri", "/tenants/{tenantId}/roles/{roleName}/users/{username}");
    properties.setProperty("action.role.queryUser.to", "queryUser(String tenantId, String roleName, String username)");
    properties.setProperty("action.role.queryUser.permission", "io.vlingo.xoom.auth.RoleQuery");
    
    return properties;
  }

  public static Properties tenantResourceProperties() {
    final Properties properties = new Properties();

    properties.setProperty("server.http.port", "8080");
    properties.setProperty("server.dispatcher.pool", "1");
    properties.setProperty("server.buffer.pool.size", "200");
    properties.setProperty("server.message.buffer.size", "10240");
    properties.setProperty("server.probe.interval", "10");
    properties.setProperty("server.probe.timeout", "10");
    properties.setProperty("server.processor.pool.size", "1");
    properties.setProperty("server.request.missing.content.timeout", "100");

    properties.setProperty("resource.name.tenant", "[subscribe, activate, deactivate, description, name, provisionGroup, provisionPermission, provisionRole, registerUser, queryTenant, queryGroups, queryPermissions, queryRoles, queryUsers]");
    properties.setProperty("resource.tenant.handler", "io.vlingo.xoom.auth.infra.resource.TenantResource");
    properties.setProperty("resource.tenant.pool", "10");
    properties.setProperty("resource.tenant.disallowPathParametersWithSlash", "true");

    properties.setProperty("action.tenant.subscribe.method", "POST");
    properties.setProperty("action.tenant.subscribe.uri", "/tenants");
    properties.setProperty("action.tenant.subscribe.to", "subscribeFor(body:io.vlingo.xoom.auth.infra.resource.TenantData tenantData)");
    properties.setProperty("action.tenant.subscribe.permission", "io.vlingo.xoom.auth.TenantRepresentative");

    properties.setProperty("action.tenant.activate.method", "PATCH");
    properties.setProperty("action.tenant.activate.uri", "/tenants/{tenantId}/activate");
    properties.setProperty("action.tenant.activate.to", "activate(String tenantId)");
    properties.setProperty("action.tenant.activate.permission", "io.vlingo.xoom.auth.TenantRepresentative");

    properties.setProperty("action.tenant.deactivate.method", "PATCH");
    properties.setProperty("action.tenant.deactivate.uri", "/tenants/{tenantId}/deactivate");
    properties.setProperty("action.tenant.deactivate.to", "deactivate(String tenantId)");
    properties.setProperty("action.tenant.deactivate.permission", "io.vlingo.xoom.auth.TenantRepresentative");

    properties.setProperty("action.tenant.description.method", "PATCH");
    properties.setProperty("action.tenant.description.uri", "/tenants/{tenantId}/description");
    properties.setProperty("action.tenant.description.to", "changeDescription(String tenantId, body:java.lang.String description)");
    properties.setProperty("action.tenant.description.permission", "io.vlingo.xoom.auth.TenantRepresentative");

    properties.setProperty("action.tenant.name.method", "PATCH");
    properties.setProperty("action.tenant.name.uri", "/tenants/{tenantId}/name");
    properties.setProperty("action.tenant.name.to", "changeName(String tenantId, body:java.lang.String name)");
    properties.setProperty("action.tenant.name.permission", "io.vlingo.xoom.auth.TenantRepresentative");

    properties.setProperty("action.tenant.provisionGroup.method", "POST");
    properties.setProperty("action.tenant.provisionGroup.uri", "/tenants/{tenantId}/groups");
    properties.setProperty("action.tenant.provisionGroup.to", "provisionGroup(String tenantId, body:io.vlingo.xoom.auth.infra.resource.GroupData groupData)");
    properties.setProperty("action.tenant.provisionGroup.permission", "io.vlingo.xoom.auth.TenantRepresentative");

    properties.setProperty("action.tenant.provisionPermission.method", "POST");
    properties.setProperty("action.tenant.provisionPermission.uri", "/tenants/{tenantId}/permissions");
    properties.setProperty("action.tenant.provisionPermission.to", "provisionPermission(String tenantId, body:io.vlingo.xoom.auth.infra.resource.PermissionData permissionData)");
    properties.setProperty("action.tenant.provisionPermission.permission", "io.vlingo.xoom.auth.TenantRepresentative");

    properties.setProperty("action.tenant.provisionRole.method", "POST");
    properties.setProperty("action.tenant.provisionRole.uri", "/tenants/{tenantId}/roles");
    properties.setProperty("action.tenant.provisionRole.to", "provisionRole(String tenantId, body:io.vlingo.xoom.auth.infra.resource.RoleData roleData)");
    properties.setProperty("action.tenant.provisionRole.permission", "io.vlingo.xoom.auth.TenantRepresentative");

    properties.setProperty("action.tenant.registerUser.method", "POST");
    properties.setProperty("action.tenant.registerUser.uri", "/tenants/{tenantId}/users");
    properties.setProperty("action.tenant.registerUser.to", "registerUser(String tenantId, body:io.vlingo.xoom.auth.infra.resource.UserRegistrationData userData)");
    properties.setProperty("action.tenant.registerUser.permission", "io.vlingo.xoom.auth.TenantRepresentative");

    properties.setProperty("action.tenant.queryTenant.method", "GET");
    properties.setProperty("action.tenant.queryTenant.uri", "/tenants/{tenantId}");
    properties.setProperty("action.tenant.queryTenant.to", "queryTenant(String tenantId)");

    properties.setProperty("action.tenant.queryGroups.method", "GET");
    properties.setProperty("action.tenant.queryGroups.uri", "/tenants/{tenantId}/groups");
    properties.setProperty("action.tenant.queryGroups.to", "queryGroups(String tenantId)");
    properties.setProperty("action.tenant.queryGroups.permission", "io.vlingo.xoom.auth.TenantQuery");

    properties.setProperty("action.tenant.queryPermissions.method", "GET");
    properties.setProperty("action.tenant.queryPermissions.uri", "/tenants/{tenantId}/permissions");
    properties.setProperty("action.tenant.queryPermissions.to", "queryPermissions(String tenantId)");
    properties.setProperty("action.tenant.queryPermissions.permission", "io.vlingo.xoom.auth.TenantQuery");

    properties.setProperty("action.tenant.queryRoles.method", "GET");
    properties.setProperty("action.tenant.queryRoles.uri", "/tenants/{tenantId}/roles");
    properties.setProperty("action.tenant.queryRoles.to", "queryRoles(String tenantId)");
    properties.setProperty("action.tenant.queryRoles.permission", "io.vlingo.xoom.auth.TenantQuery");

    properties.setProperty("action.tenant.queryUsers.method", "GET");
    properties.setProperty("action.tenant.queryUsers.uri", "/tenants/{tenantId}/users");
    properties.setProperty("action.tenant.queryUsers.to", "queryUsers(String tenantId)");
    properties.setProperty("action.tenant.queryUsers.permission", "io.vlingo.xoom.auth.TenantQuery");

    return properties;
  }

  public static Properties userResourceProperties(final Properties properties) {
    properties.setProperty("resource.name.user", "[activate, deactivate, addCredential, removeCredential, replaceCredential, profile, queryUser, queryPermission, queryRole]");

    properties.setProperty("resource.user.handler", "io.vlingo.xoom.auth.infra.resource.UserResource");
    properties.setProperty("resource.user.pool", "5");
    properties.setProperty("resource.user.disallowPathParametersWithSlash", "true");

    properties.setProperty("action.user.activate.method", "PATCH");
    properties.setProperty("action.user.activate.uri", "/tenants/{tenantId}/users/{username}/activate");
    properties.setProperty("action.user.activate.to", "activate(String tenantId, String username)");
    properties.setProperty("action.user.activate.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.user.deactivate.method", "PATCH");
    properties.setProperty("action.user.deactivate.uri", "/tenants/{tenantId}/users/{username}/deactivate");
    properties.setProperty("action.user.deactivate.to", "deactivate(String tenantId, String username)");
    properties.setProperty("action.user.deactivate.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.user.addCredential.method", "PUT");
    properties.setProperty("action.user.addCredential.uri", "/tenants/{tenantId}/users/{username}/credentials");
    properties.setProperty("action.user.addCredential.to", "addCredential(String tenantId, String username, body:io.vlingo.xoom.auth.infra.resource.CredentialData credentialData)");
    properties.setProperty("action.user.addCredential.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.user.removeCredential.method", "DELETE");
    properties.setProperty("action.user.removeCredential.uri", "/tenants/{tenantId}/users/{username}/credentials/{authority}");
    properties.setProperty("action.user.removeCredential.to", "removeCredential(String tenantId, String username, String authority)");
    properties.setProperty("action.user.removeCredential.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.user.replaceCredential.method", "PATCH");
    properties.setProperty("action.user.replaceCredential.uri", "/tenants/{tenantId}/users/{username}/credentials/{authority}");
    properties.setProperty("action.user.replaceCredential.to", "replaceCredential(String tenantId, String username, String authority, body:io.vlingo.xoom.auth.infra.resource.CredentialData credentialData)");
    properties.setProperty("action.user.replaceCredential.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.user.profile.method", "PATCH");
    properties.setProperty("action.user.profile.uri", "/tenants/{tenantId}/users/{username}/profile");
    properties.setProperty("action.user.profile.to", "profile(String tenantId, String username, body:io.vlingo.xoom.auth.infra.resource.ProfileData profileData)");
    properties.setProperty("action.user.profile.permission", "io.vlingo.xoom.auth.Administrator");

    properties.setProperty("action.user.queryUser.method", "GET");
    properties.setProperty("action.user.queryUser.uri", "/tenants/{tenantId}/users/{username}");
    properties.setProperty("action.user.queryUser.to", "queryUser(String tenantId, String username)");
    properties.setProperty("action.user.queryUser.permission", "io.vlingo.xoom.auth.UserQuery");

    properties.setProperty("action.user.queryPermission.method", "GET");
    properties.setProperty("action.user.queryPermission.uri", "/tenants/{tenantId}/users/{username}/permissions/{permissionName}");
    properties.setProperty("action.user.queryPermission.to", "queryPermission(String tenantId, String username, String permissionName)");
    properties.setProperty("action.user.queryPermission.permission", "io.vlingo.xoom.auth.UserQuery");

    properties.setProperty("action.user.queryRole.method", "GET");
    properties.setProperty("action.user.queryRole.uri", "/tenants/{tenantId}/users/{username}/roles/{roleName}");
    properties.setProperty("action.user.queryRole.to", "queryRole(String tenantId, String username, String roleName)");
    properties.setProperty("action.user.queryRole.permission", "io.vlingo.xoom.auth.UserQuery");

    return properties;
  }
}
