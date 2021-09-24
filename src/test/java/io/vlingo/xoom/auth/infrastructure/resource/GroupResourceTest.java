// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infrastructure.resource;

import static io.vlingo.xoom.common.serialization.JsonSerialization.deserialized;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.vlingo.xoom.auth.infrastructure.*;

import io.vlingo.xoom.http.Response;
import org.junit.jupiter.api.Test;

public class GroupResourceTest extends ResourceTest {
  private GroupData groupData;
  private GroupData groupGroupData;
  private PermissionData permissionData;
  private RoleData roleData;
  private TenantData tenantData;
  private UserRegistrationData userData;

  @Test
  public void testThatGroupDescriptionChanges() {
    group();

    final Response patchGroupChangeDescription = patchGroupChangeDescriptionRequestResponse(groupData, "Changed group description.");
    assertEquals(Response.Status.Ok, patchGroupChangeDescription.status);
    final GroupData changedGroupData = deserialized(patchGroupChangeDescription.entity.content(), GroupData.class);
    assertEquals("Changed group description.", changedGroupData.description);
  }

  @Test
  public void testThatGroupGroupIsAssigned() {
    groupWithGroup("Group1", "Group 1 description.");

    final Response putGroupGroupResponse = putGroupGroupRequestResponse(groupData, groupGroupData.name);
    assertEquals(Response.Status.Ok, putGroupGroupResponse.status);
    final String location = "/tenants/" + tenantData.tenantId + "/groups/" + groupData.name + "/groups/" + groupGroupData.name;
    assertEquals(location, putGroupGroupResponse.entity.content());
    final Response getGroupGroupResponse = getGroupGroupRequestResponse(groupData, groupGroupData.name);
    assertEquals(Response.Status.Ok, getGroupGroupResponse.status);
  }

  @Test
  public void testThatGroupGroupIsUnassigned() {
    groupWithGroup("Group1", "Group 1 description.");

    final Response putGroupGroupResponse = putGroupGroupRequestResponse(groupData, groupGroupData.name);
    assertEquals(Response.Status.Ok, putGroupGroupResponse.status);
    final Response deleteGroupGroupResponse = deleteGroupGroupRequestResponse(groupData, groupGroupData.name);
    assertEquals(Response.Status.Ok, deleteGroupGroupResponse.status);
    final Response getGroupGroupResponse = getGroupGroupRequestResponse(groupData, groupGroupData.name);
    assertEquals(Response.Status.NotFound, getGroupGroupResponse.status);
  }

  @Test
  public void testThatGroupUserIsAssigned() {
    groupWithUser();

    final Response putGroupUserResponse = putGroupUserRequestResponse(groupData, userData.username);
    assertEquals(Response.Status.Ok, putGroupUserResponse.status);
    final String location = "/tenants/" + groupData.tenantId + "/groups/" + groupData.name + "/users/" + userData.username;
    assertEquals(location, putGroupUserResponse.entity.content());
    final Response getGroupUserResponse = getGroupUserRequestResponse(groupData, userData.username);
    assertEquals(Response.Status.Ok, getGroupUserResponse.status);
  }

  @Test
  public void testThatGroupUserIsUnassigned() {
    groupWithUser();

    final Response putGroupUserResponse = putGroupUserRequestResponse(groupData, userData.username);
    assertEquals(Response.Status.Ok, putGroupUserResponse.status);
    final Response deleteGroupUserResponse = deleteGroupUserRequestResponse(groupData, userData.username);
    assertEquals(Response.Status.Ok, deleteGroupUserResponse.status);
    final Response getGroupUserResponse = getGroupUserRequestResponse(groupData, userData.username);
    assertEquals(Response.Status.NotFound, getGroupUserResponse.status);
  }

  @Test
  public void testThatGroupHasRolePermission() {
    this.groupWithRoleWithPermission();

    final Response putRolePermissionResponse = putRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.Ok, putRolePermissionResponse.status);
    final Response putRoleGroupResponse = putRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Status.Ok, putRoleGroupResponse.status);
    final Response getGroupPermissionResponse = getGroupPermissionRequestResponse(groupData, permissionData.name);
    assertEquals(Response.Status.Ok, getGroupPermissionResponse.status);
  }

  @Test
  public void testThatGroupQueries() {
    group();

    final Response getGroupResponse = getGroupRequestResponse(groupData);
    assertEquals(Response.Status.Ok, getGroupResponse.status);
    final GroupData queriedGroup = deserialized(getGroupResponse.entity.content(), GroupData.class);
    assertEquals(groupData.tenantId, queriedGroup.tenantId);
    assertEquals(groupData.name, queriedGroup.name);
    assertEquals(groupData.description, queriedGroup.description);
  }

  @Test
  public void testThatGroupGroupQueries() {
    groupWithGroup("Group1", "Group 1 description.");

    final Response putGroupGroupResponse = putGroupGroupRequestResponse(groupData, groupGroupData.name);
    assertEquals(Response.Status.Ok, putGroupGroupResponse.status);
    final Response getGroupGroupResponse = getGroupGroupRequestResponse(groupData, groupGroupData.name);
    assertEquals(Response.Status.Ok, getGroupGroupResponse.status);
    final GroupData queriedGroup = deserialized(getGroupGroupResponse.entity.content(), GroupData.class);
    assertEquals(groupGroupData.tenantId, queriedGroup.tenantId);
    assertEquals(groupGroupData.name, queriedGroup.name);
    assertEquals(groupGroupData.description, queriedGroup.description);
  }

  @Test
  public void testThatGroupPermissionQueries() {
    this.groupWithRoleWithPermission();

    final Response putRolePermissionResponse = putRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.Ok, putRolePermissionResponse.status);
    final Response putRoleGroupResponse = putRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Status.Ok, putRoleGroupResponse.status);
    final Response getGroupPermissionResponse = getGroupPermissionRequestResponse(groupData, permissionData.name);
    assertEquals(Response.Status.Ok, getGroupPermissionResponse.status);
    final PermissionData queriedPermission = deserialized(getGroupPermissionResponse.entity.content(), PermissionData.class);
    assertEquals(permissionData.tenantId, queriedPermission.tenantId);
    assertEquals(permissionData.name, queriedPermission.name);
    assertEquals(permissionData.description, queriedPermission.description);
  }

  @Test
  public void testThatGroupRoleQueries() {
    this.groupWithRoleWithPermission();

    final Response putRolePermissionResponse = putRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.Ok, putRolePermissionResponse.status);
    final Response putRoleGroupResponse = putRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Status.Ok, putRoleGroupResponse.status);
    final Response getGroupRoleResponse = getGroupRoleRequestResponse(groupData, roleData.name);
    assertEquals(Response.Status.Ok, getGroupRoleResponse.status);
    final RoleData queriedRole = deserialized(getGroupRoleResponse.entity.content(), RoleData.class);
    assertEquals(roleData.tenantId, queriedRole.tenantId);
    assertEquals(roleData.name, queriedRole.name);
    assertEquals(roleData.description, queriedRole.description);
  }

  @Test
  public void testThatGroupUserQueries() {
    groupWithUser();

    final Response putGroupUserResponse = putGroupUserRequestResponse(groupData, userData.username);
    assertEquals(Response.Status.Ok, putGroupUserResponse.status);
    final Response getGroupUserResponse = getGroupUserRequestResponse(groupData, userData.username);
    assertEquals(Response.Status.Ok, getGroupUserResponse.status);
    final MinimalUserData queriedUser = deserialized(getGroupUserResponse.entity.content(), MinimalUserData.class);
    assertEquals(userData.tenantId, queriedUser.tenantId);
    assertEquals(userData.username, queriedUser.username);
    assertEquals(userData.profile.name.given, queriedUser.name.given);
    assertEquals(userData.profile.name.second, queriedUser.name.second);
    assertEquals(userData.profile.name.family, queriedUser.name.family);
  }

  private Response patchGroupChangeDescriptionRequestResponse(final GroupData groupData, final String description) {
    final String request = "PATCH /tenants/" + groupData.tenantId + "/groups/" + groupData.name + "/description HTTP/1.1\nHost: vlingo.io\nContent-Length: " + description.length() + "\n\n" + description;
    return requestResponse(request);
  }

  private Response putGroupGroupRequestResponse(final GroupData groupData, final String groupName) {
    final String request = "PUT /tenants/" + groupData.tenantId + "/groups/" + groupData.name + "/groups HTTP/1.1\nHost: vlingo.io\nContent-Length: " + groupName.length() + "\n\n" + groupName;
    return requestResponse(request);
  }

  private Response deleteGroupGroupRequestResponse(final GroupData groupData, final String groupName) {
    final String request = "DELETE /tenants/" + groupData.tenantId + "/groups/" + groupData.name + "/groups/" + groupName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response putRolePermissionRequestResponse(final RoleData roleData, final String permissionName) {
    final String request = "PUT /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/permissions HTTP/1.1\nHost: vlingo.io\nContent-Length: " + permissionName.length() + "\n\n" + permissionName;
    return requestResponse(request);
  }

  private Response putGroupUserRequestResponse(final GroupData groupData, final String username) {
    final String request = "PUT /tenants/" + groupData.tenantId + "/groups/" + groupData.name + "/users HTTP/1.1\nHost: vlingo.io\nContent-Length: " + username.length() + "\n\n" + username;
    return requestResponse(request);
  }

  private Response deleteGroupUserRequestResponse(final GroupData groupData, final String username) {
    final String request = "DELETE /tenants/" + groupData.tenantId + "/groups/" + groupData.name + "/users/" + username + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response putRoleGroupRequestResponse(final RoleData roleData, final String groupName) {
    final String request = "PUT /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/groups HTTP/1.1\nHost: vlingo.io\nContent-Length: " + groupName.length() + "\n\n" + groupName;
    return requestResponse(request);
  }

  private void group() {
    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData());
    tenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    final Response groupResponse = postProvisionGroup(tenantData.tenantId, "Group", "A group description.");
    groupData = deserialized(groupResponse.entity.content(), GroupData.class);
  }

  private void groupWithGroup(final String groupName, final String groupDescription) {
    group();

    final Response groupResponse = postProvisionGroup(tenantData.tenantId, groupName, groupDescription);
    groupGroupData = deserialized(groupResponse.entity.content(), GroupData.class);
  }

  private void groupWithRoleWithPermission() {
    group();

    final Response roleResponse = postProvisionRole(tenantData.tenantId, "Role", "A role description.");
    roleData = deserialized(roleResponse.entity.content(), RoleData.class);
    final Response permissionProvisionedResponse = postProvisionPermission(tenantData.tenantId, "Permission", "A permission description.");
    permissionData = deserialized(permissionProvisionedResponse.entity.content(), PermissionData.class);
  }

  private void groupWithUser() {
    group();

    final Response userRegistrationResponse = postRegisterUser(tenantData.tenantId, userRegistrationData(tenantData.tenantId));
    userData = deserialized(userRegistrationResponse.entity.content(), UserRegistrationData.class);
  }
}
