// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infrastructure.resource;

import static io.vlingo.xoom.common.serialization.JsonSerialization.deserialized;
import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import io.vlingo.xoom.http.Response;

public class RoleResourceTest extends ResourceTest {
  private GroupData groupData;
  private PermissionData permissionData;
  private RoleData roleData;
  private TenantData tenantData;
  private UserRegistrationData userData;

  @Test
  public void testThatRoleDescriptionChanges() {
    role();

    final Response patchRoleChangeDescription = patchRoleChangeDescriptionRequestResponse(roleData, "Changed role description.");
    assertEquals(Response.Status.Ok, patchRoleChangeDescription.status);
    final RoleData changedRoleData = deserialized(patchRoleChangeDescription.entity.content(), RoleData.class);
    assertEquals("Changed role description.", changedRoleData.description);
  }

  @Test
  public void testThatRoleGroupIsAssigned() {
    roleWithGroup();

    final Response putRoleGroupResponse = putRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Status.Ok, putRoleGroupResponse.status);
    final String location = "/tenants/" + tenantData.tenantId + "/roles/" + roleData.name + "/groups/" + groupData.name;
    assertEquals(location, putRoleGroupResponse.entity.content());
    final Response getRoleGroupResponse = getRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Status.Ok, getRoleGroupResponse.status);
  }

  @Test
  public void testThatRoleGroupIsUnassigned() {
    roleWithGroup();

    final Response putRoleGroupResponse = putRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Status.Ok, putRoleGroupResponse.status);
    final Response deleteRoleGroupResponse = deleteRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Status.Ok, deleteRoleGroupResponse.status);
    final Response getRoleGroupResponse = getRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Status.NotFound, getRoleGroupResponse.status);
  }

  @Test
  public void testThatRoleUserIsAssigned() {
    roleWithUser();

    final Response putRoleUserResponse = putRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Status.Ok, putRoleUserResponse.status);
    final String location = "/tenants/" + tenantData.tenantId + "/roles/" + roleData.name + "/users/" + userData.username;
    assertEquals(location, putRoleUserResponse.entity.content());
    final Response getRoleUserResponse = getRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Status.Ok, getRoleUserResponse.status);
  }

  @Test
  public void testThatRoleUserIsUnassigned() {
    roleWithUser();

    final Response putRoleUserResponse = putRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Status.Ok, putRoleUserResponse.status);
    final Response deleteRoleUserResponse = deleteRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Status.Ok, deleteRoleUserResponse.status);
    final Response getRoleUserResponse = getRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Status.NotFound, getRoleUserResponse.status);
  }

  @Test
  public void testThatRolePermissionAttaches() {
    roleWithPermission();

    final Response putRolePermissionResponse = putRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.Ok, putRolePermissionResponse.status);
    final String location = "/tenants/" + tenantData.tenantId + "/roles/" + roleData.name + "/permissions/" + permissionData.name;
    assertEquals(location, putRolePermissionResponse.entity.content());
    final Response getRolePermissionResponse = getRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.Ok, getRolePermissionResponse.status);
  }

  @Test
  public void testThatRolePermissionDetaches() {
    roleWithPermission();
    final Response putRolePermissionResponse = putRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.Ok, putRolePermissionResponse.status);
    final Response deleteRolePermissionResponse = deleteRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.Ok, deleteRolePermissionResponse.status);
    final Response getRolePermissionResponse = getRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.NotFound, getRolePermissionResponse.status);
  }

  @Test
  public void testThatRoleQueries() {
    role();

    final Response getRoleResponse = getRoleRequestResponse(roleData.tenantId, roleData.name);
    assertEquals(Response.Status.Ok, getRoleResponse.status);
    final RoleData queriedRole = deserialized(getRoleResponse.entity.content(), RoleData.class);
    assertEquals(roleData.tenantId, queriedRole.tenantId);
    assertEquals(roleData.name, queriedRole.name);
    assertEquals(roleData.description, queriedRole.description);
  }

  @Test
  public void testThatRoleGroupQueries() {
    roleWithGroup();

    final Response putRoleGroupResponse = putRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Status.Ok, putRoleGroupResponse.status);
    final Response getRoleGroupResponse = getRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Status.Ok, getRoleGroupResponse.status);
    final GroupData queriedRole = deserialized(getRoleGroupResponse.entity.content(), GroupData.class);
    assertEquals(groupData.tenantId, queriedRole.tenantId);
    assertEquals(groupData.name, queriedRole.name);
    assertEquals(groupData.description, queriedRole.description);
  }

  @Test
  public void testThatRolePermissionQueries() {
    roleWithPermission();

    final Response putRolePermissionResponse = putRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.Ok, putRolePermissionResponse.status);

    final Response getRolePermissionResponse = getRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.Ok, getRolePermissionResponse.status);
    final PermissionData queriedPermission = deserialized(getRolePermissionResponse.entity.content(), PermissionData.class);
    assertEquals(permissionData.tenantId, queriedPermission.tenantId);
    assertEquals(permissionData.name, queriedPermission.name);
    assertEquals(permissionData.description, queriedPermission.description);
  }

  @Test
  public void testThatRoleUserQueries() {
    roleWithUser();

    final Response putRoleUserResponse = putRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Status.Ok, putRoleUserResponse.status);
    final Response getRoleUserResponse = getRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Status.Ok, getRoleUserResponse.status);
    final MinimalUserData queriedUser = deserialized(getRoleUserResponse.entity.content(), MinimalUserData.class);
    assertEquals(userData.tenantId, queriedUser.tenantId);
    assertEquals(userData.username, queriedUser.username);
    assertEquals(userData.profile.name.given, queriedUser.name.given);
    assertEquals(userData.profile.name.second, queriedUser.name.second);
    assertEquals(userData.profile.name.family, queriedUser.name.family);
  }

  @Override
  protected Properties resourceProperties() {
    return TestProperties.roleResourceProperties(TestProperties.tenantResourceProperties());
  }

  private Response patchRoleChangeDescriptionRequestResponse(final RoleData roleData, final String description) {
    final String request = "PATCH /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/description HTTP/1.1\nHost: vlingo.io\nContent-Length: " + description.length() + "\n\n" + description;
    return requestResponse(request);
  }

  private Response putRoleGroupRequestResponse(final RoleData roleData, final String groupName) {
    final String request = "PUT /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/groups HTTP/1.1\nHost: vlingo.io\nContent-Length: " + groupName.length() + "\n\n" + groupName;
    return requestResponse(request);
  }

  private Response deleteRoleGroupRequestResponse(final RoleData roleData, final String groupName) {
    final String request = "DELETE /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/groups/" + groupName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response putRolePermissionRequestResponse(final RoleData roleData, final String permissionName) {
    final String request = "PUT /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/permissions HTTP/1.1\nHost: vlingo.io\nContent-Length: " + permissionName.length() + "\n\n" + permissionName;
    return requestResponse(request);
  }

  private Response deleteRolePermissionRequestResponse(RoleData roleData, String permissionName) {
    final String request = "DELETE /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/permissions/" + permissionName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response putRoleUserRequestResponse(final RoleData roleData, final String username) {
    final String request = "PUT /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/users HTTP/1.1\nHost: vlingo.io\nContent-Length: " + username.length() + "\n\n" + username;
    return requestResponse(request);
  }

  private Response deleteRoleUserRequestResponse(final RoleData roleData, final String username) {
    final String request = "DELETE /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/users/" + username + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private void role() {
    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData());
    tenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    final Response roleResponse = postProvisionRole(tenantData.tenantId, "Role", "A role description.");
    roleData = deserialized(roleResponse.entity.content(), RoleData.class);
  }

  private void roleWithGroup() {
    role();

    final Response groupResponse = postProvisionGroup(tenantData.tenantId, "Group", "A group description.");
    groupData = deserialized(groupResponse.entity.content(), GroupData.class);
  }

  private void roleWithPermission() {
    role();

    final Response permissionProvisionedResponse = postProvisionPermission(tenantData.tenantId, "Permission", "A permission description.");
    permissionData = deserialized(permissionProvisionedResponse.entity.content(), PermissionData.class);
  }

  private void roleWithUser() {
    role();

    final Response userRegistrationResponse = postRegisterUser(tenantData.tenantId, userRegistrationData(tenantData.tenantId));
    userData = deserialized(userRegistrationResponse.entity.content(), UserRegistrationData.class);
  }
}
