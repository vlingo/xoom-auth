// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import java.util.Properties;

public class TestProperties {

  public static Properties roleResourceProperties() {
    final Properties properties = tenantResourceProperties();
    
    properties.setProperty("resource.name.role", "[description, assignGroup, unassignGroup, assignUser, unassignUser, attach, detach, role, permission, group, user]");

    properties.setProperty("resource.role.handler", "io.vlingo.auth.infra.resource.RoleResource");
    properties.setProperty("resource.role.pool", "10");
    properties.setProperty("resource.role.disallowPathParametersWithSlash", "true");

    properties.setProperty("action.role.description.method", "PATCH");
    properties.setProperty("action.role.description.uri", "/tenants/{tenantId}/roles/{roleName}/description");
    properties.setProperty("action.role.description.to", "changeDescription(String tenantId, String roleName, body:java.lang.String description)");
    properties.setProperty("action.role.description.permission", "io.vlingo.auth.Administrator");

    properties.setProperty("action.role.assignGroup.method", "PUT");
    properties.setProperty("action.role.assignGroup.uri", "/tenants/{tenantId}/roles/{roleName}/groups");
    properties.setProperty("action.role.assignGroup.to", "assignGroup(String tenantId, String roleName, body:java.lang.String groupName)");
    properties.setProperty("action.role.assignGroup.permission", "io.vlingo.auth.Administrator");

    properties.setProperty("action.role.unassignGroup.method", "DELETE");
    properties.setProperty("action.role.unassignGroup.uri", "/tenants/{tenantId}/roles/{roleName}/groups/{groupName}");
    properties.setProperty("action.role.unassignGroup.to", "unassignGroup(String tenantId, String roleName, String groupName)");
    properties.setProperty("action.role.unassignGroup.permission", "io.vlingo.auth.Administrator");

    properties.setProperty("action.role.assignUser.method", "PUT");
    properties.setProperty("action.role.assignUser.uri", "/tenants/{tenantId}/roles/{roleName}/users");
    properties.setProperty("action.role.assignUser.to", "assignUser(String tenantId, String roleName, body:java.lang.String username)");
    properties.setProperty("action.role.assignUser.permission", "io.vlingo.auth.Administrator");

    properties.setProperty("action.role.unassignUser.method", "DELETE");
    properties.setProperty("action.role.unassignUser.uri", "/tenants/{tenantId}/roles/{roleName}/users/{username}");
    properties.setProperty("action.role.unassignUser.to", "unassignUser(String tenantId, String roleName, String username)");
    properties.setProperty("action.role.unassignUser.permission", "io.vlingo.auth.Administrator");

    properties.setProperty("action.role.attach.method", "PUT");
    properties.setProperty("action.role.attach.uri", "/tenants/{tenantId}/roles/{roleName}/permissions");
    properties.setProperty("action.role.attach.to", "attach(String tenantId, String roleName, body:java.lang.String permissionName)");
    properties.setProperty("action.role.attach.permission", "io.vlingo.auth.Administrator");

    properties.setProperty("action.role.detach.method", "DELETE");
    properties.setProperty("action.role.detach.uri", "/tenants/{tenantId}/roles/{roleName}/permissions/{permissionName}");
    properties.setProperty("action.role.detach.to", "detach(String tenantId, String roleName, String permissionName)");
    properties.setProperty("action.role.detach.permission", "io.vlingo.auth.Administrator");

    properties.setProperty("action.role.role.method", "GET");
    properties.setProperty("action.role.role.uri", "/tenants/{tenantId}/roles/{roleName}");
    properties.setProperty("action.role.role.to", "role(String tenantId, String roleName)");
    properties.setProperty("action.role.role.permission", "io.vlingo.auth.RoleQuery");

    properties.setProperty("action.role.permission.method", "GET");
    properties.setProperty("action.role.permission.uri", "/tenants/{tenantId}/roles/{roleName}/permissions/{permissionName}");
    properties.setProperty("action.role.permission.to", "permission(String tenantId, String roleName, String permissionName)");
    properties.setProperty("action.role.permission.permission", "io.vlingo.auth.RoleQuery");

    properties.setProperty("action.role.group.method", "GET");
    properties.setProperty("action.role.group.uri", "/tenants/{tenantId}/roles/{roleName}/groups/{groupName}");
    properties.setProperty("action.role.group.to", "group(String tenantId, String roleName, String groupName)");
    properties.setProperty("action.role.group.permission", "io.vlingo.auth.RoleQuery");

    properties.setProperty("action.role.user.method", "GET");
    properties.setProperty("action.role.user.uri", "/tenants/{tenantId}/roles/{roleName}/users/{username}");
    properties.setProperty("action.role.user.to", "user(String tenantId, String roleName, String username)");
    properties.setProperty("action.role.user.permission", "io.vlingo.auth.RoleQuery");
    
    return properties;
  }

  public static Properties tenantResourceProperties() {
    final Properties properties = new Properties();

    properties.setProperty("server.http.port", "8080");
    properties.setProperty("server.dispatcher.pool", "10");
    properties.setProperty("server.buffer.pool.size", "2000");
    properties.setProperty("server.message.buffer.size", "65535");
    properties.setProperty("server.probe.interval", "10");
    properties.setProperty("server.probe.timeout", "10");
    properties.setProperty("server.request.missing.content.timeout", "100");

    properties.setProperty("resource.name.tenant", "[subscribe, queryTenant, activate, deactivate, description, name, provisionGroup, provisionRole, registerUser]");
    properties.setProperty("resource.tenant.handler", "io.vlingo.auth.infra.resource.TenantResource");
    properties.setProperty("resource.tenant.pool", "10");
    properties.setProperty("resource.tenant.disallowPathParametersWithSlash", "true");

    properties.setProperty("action.tenant.subscribe.method", "POST");
    properties.setProperty("action.tenant.subscribe.uri", "/tenants");
    properties.setProperty("action.tenant.subscribe.to", "subscribeFor(body:io.vlingo.auth.infra.resource.TenantData tenantData)");
    properties.setProperty("action.tenant.subscribe.permission", "io.vlingo.auth.TenantRepresentative");

    properties.setProperty("action.tenant.queryTenant.method", "GET");
    properties.setProperty("action.tenant.queryTenant.uri", "/tenants/{tenantId}");
    properties.setProperty("action.tenant.queryTenant.to", "queryTenant(String tenantId)");

    properties.setProperty("action.tenant.activate.method", "PATCH");
    properties.setProperty("action.tenant.activate.uri", "/tenants/{tenantId}/activate");
    properties.setProperty("action.tenant.activate.to", "activate(String tenantId)");
    properties.setProperty("action.tenant.activate.permission", "io.vlingo.auth.TenantRepresentative");

    properties.setProperty("action.tenant.deactivate.method", "PATCH");
    properties.setProperty("action.tenant.deactivate.uri", "/tenants/{tenantId}/deactivate");
    properties.setProperty("action.tenant.deactivate.to", "deactivate(String tenantId)");
    properties.setProperty("action.tenant.deactivate.permission", "io.vlingo.auth.TenantRepresentative");

    properties.setProperty("action.tenant.description.method", "PATCH");
    properties.setProperty("action.tenant.description.uri", "/tenants/{tenantId}/description");
    properties.setProperty("action.tenant.description.to", "changeDescription(String tenantId, body:java.lang.String description)");
    properties.setProperty("action.tenant.description.permission", "io.vlingo.auth.TenantRepresentative");

    properties.setProperty("action.tenant.name.method", "PATCH");
    properties.setProperty("action.tenant.name.uri", "/tenants/{tenantId}/name");
    properties.setProperty("action.tenant.name.to", "changeName(String tenantId, body:java.lang.String name)");
    properties.setProperty("action.tenant.name.permission", "io.vlingo.auth.TenantRepresentative");

    properties.setProperty("action.tenant.provisionGroup.method", "POST");
    properties.setProperty("action.tenant.provisionGroup.uri", "/tenants/{tenantId}/groups");
    properties.setProperty("action.tenant.provisionGroup.to", "provisionGroup(String tenantId, body:io.vlingo.auth.infra.resource.GroupData groupData)");
    properties.setProperty("action.tenant.provisionGroup.permission", "io.vlingo.auth.TenantRepresentative");

    properties.setProperty("action.tenant.provisionRole.method", "POST");
    properties.setProperty("action.tenant.provisionRole.uri", "/tenants/{tenantId}/roles");
    properties.setProperty("action.tenant.provisionRole.to", "provisionRole(String tenantId, body:io.vlingo.auth.infra.resource.RoleData roleData)");
    properties.setProperty("action.tenant.provisionRole.permission", "io.vlingo.auth.TenantRepresentative");

    properties.setProperty("action.tenant.registerUser.method", "POST");
    properties.setProperty("action.tenant.registerUser.uri", "/tenants/{tenantId}/users");
    properties.setProperty("action.tenant.registerUser.to", "registerUser(String tenantId, body:io.vlingo.auth.infra.resource.UserRegistrationData userData)");
    properties.setProperty("action.tenant.registerUser.permission", "io.vlingo.auth.TenantRepresentative");

    return properties;
  }

}
