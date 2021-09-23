// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infrastructure.resource;

import com.google.gson.reflect.TypeToken;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;

import static io.vlingo.xoom.common.serialization.JsonSerialization.deserialized;
import static io.vlingo.xoom.common.serialization.JsonSerialization.deserializedList;
import static org.junit.jupiter.api.Assertions.*;

public class TenantResourceTest extends ResourceTest {

  @Test
  public void testThatTenantSubscribes() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    assertNotNull(createdResponse.headers.headerOf(ResponseHeader.Location));
    assertEquals(tenantData.withTenantId(createdTenantData.tenantId), createdTenantData);

    final String location = createdResponse.headerOf(ResponseHeader.Location).value;
    final Response getTenantResponse = getTenantRequestResponse(location);
    assertEquals(Response.Status.Ok, getTenantResponse.status);
    assertNotNull(getTenantResponse.entity);
    assertNotNull(getTenantResponse.entity.content());
    assertFalse(getTenantResponse.entity.content().isEmpty());
    final TenantData getTenantData = deserialized(getTenantResponse.entity.content(), TenantData.class);
    assertEquals(tenantData.withTenantId(getTenantData.tenantId), getTenantData);
  }

  @Test
  public void testThatTenantDeactivatesAndActivates() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    assertTrue(createdTenantData.active);

    final String location = createdResponse.headerOf(ResponseHeader.Location).value;

    final Response patchTenantDeactive = patchTenantDeactivateRequestResponse(createdTenantData.tenantId);
    assertEquals(Response.Status.Ok, patchTenantDeactive.status);
    final TenantData deactivatedTenantData = deserialized(patchTenantDeactive.entity.content(), TenantData.class);
    assertFalse(deactivatedTenantData.active);

    final Response getTenantResponse1 = getTenantRequestResponse(location);
    assertEquals(Response.Status.Ok, getTenantResponse1.status);
    final TenantData getDeactivatedTenantData = deserialized(getTenantResponse1.entity.content(), TenantData.class);
    assertFalse(getDeactivatedTenantData.active);

    final Response patchTenantActivate = this.patchTenantActivateRequestResponse(createdTenantData.tenantId);
    assertEquals(Response.Status.Ok, patchTenantActivate.status);
    final TenantData activatedTenantData = deserialized(patchTenantActivate.entity.content(), TenantData.class);
    assertTrue(activatedTenantData.active);

    final Response getTenantResponse2 = getTenantRequestResponse(location);
    assertEquals(Response.Status.Ok, getTenantResponse2.status);
    final TenantData getActivatedTenantData = deserialized(getTenantResponse2.entity.content(), TenantData.class);
    assertTrue(getActivatedTenantData.active);
  }

  @Test
  public void testThatTenantChangesDescription() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);

    final String location = createdResponse.headerOf(ResponseHeader.Location).value;

    final Response patchTenantDescription = this.patchTenantDescriptionRequestResponse(createdTenantData.tenantId, "New-Description");
    assertEquals(Response.Status.Ok, patchTenantDescription.status);
    final TenantData redescribedTenantData = deserialized(patchTenantDescription.entity.content(), TenantData.class);
    assertEquals("New-Description", redescribedTenantData.description);

    final Response getTenantResponse1 = getTenantRequestResponse(location);
    assertEquals(Response.Status.Ok, getTenantResponse1.status);
    final TenantData getRenamedTenantData = deserialized(getTenantResponse1.entity.content(), TenantData.class);
    assertEquals("New-Description", getRenamedTenantData.description);
  }

  @Test
  public void testThatTenantChangesName() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);

    final String location = createdResponse.headerOf(ResponseHeader.Location).value;

    final Response patchTenantName = this.patchTenantNameRequestResponse(createdTenantData.tenantId, "New-Name");
    assertEquals(Response.Status.Ok, patchTenantName.status);
    final TenantData renamedTenantData = deserialized(patchTenantName.entity.content(), TenantData.class);
    assertEquals("New-Name", renamedTenantData.name);

    final Response getTenantResponse1 = getTenantRequestResponse(location);
    assertEquals(Response.Status.Ok, getTenantResponse1.status);
    final TenantData getRenamedTenantData = deserialized(getTenantResponse1.entity.content(), TenantData.class);
    assertEquals("New-Name", getRenamedTenantData.name);
  }

  @Test
  public void testThatTenantProvisionsGroups() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);

    final Response groupProvisionedResponse = postProvisionGroup(createdTenantData.tenantId, "Group", "A group description.");
    assertEquals(Response.Status.Created, groupProvisionedResponse.status);
    final GroupData groupData = deserialized(groupProvisionedResponse.entity.content(), GroupData.class);
    assertEquals(createdTenantData.tenantId, groupData.tenantId);
    assertEquals("Group", groupData.name);
    assertEquals("A group description.", groupData.description);
  }

  @Test
  public void testThatTenantProvisionsPermissions() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);

    final Response permissionProvisionedResponse = postProvisionPermission(createdTenantData.tenantId, "Permission", "A permission description.");
    assertEquals(Response.Status.Created, permissionProvisionedResponse.status);
    final PermissionData permissionData = deserialized(permissionProvisionedResponse.entity.content(), PermissionData.class);
    assertEquals(createdTenantData.tenantId, permissionData.tenantId);
    assertEquals("Permission", permissionData.name);
    assertEquals("A permission description.", permissionData.description);
  }

  @Test
  public void testThatTenantProvisionsRoles() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);

    final Response roleProvisionedResponse = postProvisionRole(createdTenantData.tenantId, "Role", "A role description.");
    assertEquals(Response.Status.Created, roleProvisionedResponse.status);
    final RoleData roleData = deserialized(roleProvisionedResponse.entity.content(), RoleData.class);
    assertEquals(createdTenantData.tenantId, roleData.tenantId);
    assertEquals("Role", roleData.name);
    assertEquals("A role description.", roleData.description);
  }

  @Test
  public void testThatTenantRegistersUsers() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);

    final UserRegistrationData userRegData = userRegistrationData(createdTenantData.tenantId);
    final Response registerUserResponse = postRegisterUser(createdTenantData.tenantId, userRegData);
    assertEquals(Response.Status.Created, registerUserResponse.status);
    final UserRegistrationData userRegistrateredData = deserialized(registerUserResponse.entity.content(), UserRegistrationData.class);
    assertEquals(createdTenantData.tenantId, userRegistrateredData.tenantId);
    assertEquals(userRegData.username, userRegistrateredData.username);
    assertEquals(userRegData.profile.name.given, userRegistrateredData.profile.name.given);
    assertEquals(userRegData.profile.name.second, userRegistrateredData.profile.name.second);
    assertEquals(userRegData.profile.name.family, userRegistrateredData.profile.name.family);
    assertEquals(userRegData.profile.emailAddress, userRegistrateredData.profile.emailAddress);
    assertEquals(userRegData.profile.phone, userRegistrateredData.profile.phone);
    assertEquals(userRegData.credentials.stream().findFirst().get().authority, userRegistrateredData.credentials.stream().findFirst().get().authority);
    assertEquals(userRegData.credentials.stream().findFirst().get().id, userRegistrateredData.credentials.stream().findFirst().get().id);
    assertNotEquals(userRegData.credentials.stream().findFirst().get().secret, userRegistrateredData.credentials.stream().findFirst().get().secret);
    assertEquals("VLINGO", userRegistrateredData.credentials.stream().findFirst().get().type);
    assertTrue(userRegistrateredData.active);
  }

  @Test
  public void testThatTenantQueriesTenant() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    final String location = createdResponse.headerOf(ResponseHeader.Location).value;
    final Response getTenantResponse = getTenantRequestResponse(location);
    assertEquals(Response.Status.Ok, getTenantResponse.status);
    final TenantData queriedTenantData = deserialized(getTenantResponse.entity.content(), TenantData.class);
    assertEquals(createdTenantData.tenantId, queriedTenantData.tenantId);
    assertEquals(createdTenantData.name, queriedTenantData.name);
    assertEquals(createdTenantData.description, queriedTenantData.description);
  }

  @Test
  public void testThatTenantQueriesGroups() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    final String location = createdResponse.headerOf(ResponseHeader.Location).value;

    final Response group1ProvisionedResponse = postProvisionGroup(createdTenantData.tenantId, "Group1", "A group1 description.");
    assertEquals(Response.Status.Created, group1ProvisionedResponse.status);
    final Response group2ProvisionedResponse = postProvisionGroup(createdTenantData.tenantId, "Group2", "A group2 description.");
    assertEquals(Response.Status.Created, group2ProvisionedResponse.status);

    final Response getTenantGroupsResponse = getTenantGroupsRequestResponse(location);
    assertEquals(Response.Status.Ok, getTenantGroupsResponse.status);
    final Type listType = new TypeToken<List<GroupData>>(){}.getType();
    final List<GroupData> groupData = deserializedList(getTenantGroupsResponse.entity.content(), listType);
    assertEquals(2, groupData.size());
  }

  @Test
  public void testThatTenantQueriesPermissions() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    final String location = createdResponse.headerOf(ResponseHeader.Location).value;

    final Response permission1ProvisionedResponse = postProvisionPermission(createdTenantData.tenantId, "Permission1", "A permission1 description.");
    assertEquals(Response.Status.Created, permission1ProvisionedResponse.status);
    final Response permission2ProvisionedResponse = postProvisionPermission(createdTenantData.tenantId, "Permission2", "A permission2 description.");
    assertEquals(Response.Status.Created, permission2ProvisionedResponse.status);

    final Response getTenantPermissionsResponse = getTenantPermissionsRequestResponse(location);
    assertEquals(Response.Status.Ok, getTenantPermissionsResponse.status);
    final Type listType = new TypeToken<List<PermissionData>>(){}.getType();
    final List<PermissionData> permissionData = deserializedList(getTenantPermissionsResponse.entity.content(), listType);
    assertEquals(2, permissionData.size());
  }

  @Test
  public void testThatTenantQueriesRoles() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    final String location = createdResponse.headerOf(ResponseHeader.Location).value;

    final Response role1ProvisionedResponse = postProvisionRole(createdTenantData.tenantId, "Role1", "A role1 description.");
    assertEquals(Response.Status.Created, role1ProvisionedResponse.status);
    final Response role2ProvisionedResponse = postProvisionRole(createdTenantData.tenantId, "Role2", "A role2 description.");
    assertEquals(Response.Status.Created, role2ProvisionedResponse.status);

    final Response getTenantRolesResponse = getTenantRolesRequestResponse(location);
    assertEquals(Response.Status.Ok, getTenantRolesResponse.status);
    final Type listType = new TypeToken<List<RoleData>>(){}.getType();
    final List<RoleData> roleData = deserializedList(getTenantRolesResponse.entity.content(), listType);
    assertEquals(2, roleData.size());
  }

  @Test
  public void testThatTenantQueriesUsers() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Status.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    final String location = createdResponse.headerOf(ResponseHeader.Location).value;

    final UserRegistrationData user1RegData = userRegistrationData(createdTenantData.tenantId, 1);
    final Response registerUser1Response = postRegisterUser(createdTenantData.tenantId, user1RegData);
    assertEquals(Response.Status.Created, registerUser1Response.status);
    final UserRegistrationData user2RegData = userRegistrationData(createdTenantData.tenantId, 2);
    final Response registerUser2Response = postRegisterUser(createdTenantData.tenantId, user2RegData);
    assertEquals(Response.Status.Created, registerUser2Response.status);

    final Response getTenantUsersResponse = getTenantUsersRequestResponse(location);
    assertEquals(Response.Status.Ok, getTenantUsersResponse.status);
    final Type listType = new TypeToken<List<MinimalUserData>>(){}.getType();
    final List<MinimalUserData> userData = deserializedList(getTenantUsersResponse.entity.content(), listType);
    assertEquals(2, userData.size());
  }

  private Response patchTenantActivateRequestResponse(final String tenantId) {
    final String request = "PATCH /tenants/" + tenantId + "/activate HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response patchTenantDeactivateRequestResponse(final String tenantId) {
    final String request = "PATCH /tenants/" + tenantId + "/deactivate HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response patchTenantDescriptionRequestResponse(final String tenantId, final String description) {
    final String request = "PATCH /tenants/" + tenantId + "/description HTTP/1.1\nHost: vlingo.io\nContent-Length: " + description.length() + "\n\n" + description;
    return requestResponse(request);
  }

  private Response patchTenantNameRequestResponse(final String tenantId, final String name) {
    final String request = "PATCH /tenants/" + tenantId + "/name HTTP/1.1\nHost: vlingo.io\nContent-Length: " + name.length() + "\n\n" + name;
    return requestResponse(request);
  }
}
