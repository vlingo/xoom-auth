// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import static io.vlingo.http.resource.serialization.JsonSerialization.deserialized;
import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import io.vlingo.http.Response;

public class RoleResourceTest extends ResourceTest {
  private GroupData groupData;
  private RoleData roleData;
  private TenantData tenantData;
  private MinimalUserData userData;

  @Test
  public void testThatRoleDescriptionChanges() {
    role();
    
    final Response patchRoleChangeDescription = patchRoleChangeDescriptionRequestResponse(roleData, "Changed role description.");
    assertEquals(Response.Ok, patchRoleChangeDescription.status);
    final RoleData changedRoleData = deserialized(patchRoleChangeDescription.entity.content, RoleData.class);
    assertEquals("Changed role description.", changedRoleData.description);
  }

  @Test
  public void testThatRoleGroupIsAssigned() {
    roleWithGroup();
    
    final Response putRoleGroupResponse = putRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Ok, putRoleGroupResponse.status);
    final String location = "/tenants/" + tenantData.tenantId + "/roles/" + roleData.name + "/groups/" + groupData.name;
    assertEquals(location, putRoleGroupResponse.entity.content);
  }
  
  @Test
  public void testThatRoleGroupIsUnassigned() {
    roleWithGroup();
    
    final Response putRoleGroupResponse = putRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Ok, putRoleGroupResponse.status);
    final Response deleteRoleGroupResponse = deleteRoleGroupRequestResponse(roleData, groupData.name);
    assertEquals(Response.Ok, deleteRoleGroupResponse.status);
  }

  @Test
  public void testThatRoleUserIsAssigned() {
    roleWithUser();
    
    final Response putRoleUserResponse = putRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Ok, putRoleUserResponse.status);
    final String location = "/tenants/" + tenantData.tenantId + "/roles/" + roleData.name + "/users/" + userData.username;
    assertEquals(location, putRoleUserResponse.entity.content);
  }
  
  @Test
  public void testThatRoleUserIsUnassigned() {
    roleWithUser();
    
    final Response putRoleUserResponse = putRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Ok, putRoleUserResponse.status);
    final Response deleteRoleUserResponse = deleteRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Ok, deleteRoleUserResponse.status);
  }

  protected Properties resourceProperties() {
    return TestProperties.roleResourceProperties();
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
    tenantData = deserialized(createdResponse.entity.content, TenantData.class);
    final Response roleResponse = postProvisionRole(tenantData.tenantId, "Role", "A role description.");
    roleData = deserialized(roleResponse.entity.content, RoleData.class);
  }

  private void roleWithGroup() {
    role();

    final Response groupResponse = postProvisionGroup(tenantData.tenantId, "Group", "A group description.");
    groupData = deserialized(groupResponse.entity.content, GroupData.class);
  }

  private void roleWithUser() {
    role();

    final Response userRegistrationResponse = postRegisterUser(tenantData.tenantId, userRegistrationData(tenantData.tenantId));
    userData = deserialized(userRegistrationResponse.entity.content, MinimalUserData.class);
  }
}
