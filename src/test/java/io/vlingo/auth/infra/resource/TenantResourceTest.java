// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import static io.vlingo.http.resource.serialization.JsonSerialization.deserialized;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;

public class TenantResourceTest extends ResourceTest {

  @Test
  public void testThatTenantSubscribes() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content, TenantData.class);
    assertEquals(1, progress.consumeCount.get());
    assertNotNull(createdResponse.headers.headerOf(ResponseHeader.Location));
    assertEquals(tenantData.withTenantId(createdTenantData.tenantId), createdTenantData);
    
    final String location = createdResponse.headerOf(ResponseHeader.Location).value;
    final Response getTenantResponse = getTenantRequestResponse(location);
    assertEquals(2, progress.consumeCount.get());
    assertEquals(Response.Ok, getTenantResponse.status);
    assertNotNull(getTenantResponse.entity);
    assertNotNull(getTenantResponse.entity.content);
    assertFalse(getTenantResponse.entity.content.isEmpty());
    final TenantData getTenantData = deserialized(getTenantResponse.entity.content, TenantData.class);
    assertEquals(tenantData.withTenantId(getTenantData.tenantId), getTenantData);
  }
  
  @Test
  public void testThatTenantDeactivatesAndActivates() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content, TenantData.class);
    assertTrue(createdTenantData.active);
    
    final String location = createdResponse.headerOf(ResponseHeader.Location).value;
    
    final Response patchTenantDeactive = patchTenantDeactivateRequestResponse(createdTenantData.tenantId);
    assertEquals(Response.Ok, patchTenantDeactive.status);
    final TenantData deactivatedTenantData = deserialized(patchTenantDeactive.entity.content, TenantData.class);
    assertFalse(deactivatedTenantData.active);

    final Response getTenantResponse1 = getTenantRequestResponse(location);
    assertEquals(Response.Ok, getTenantResponse1.status);
    final TenantData getDeactivatedTenantData = deserialized(getTenantResponse1.entity.content, TenantData.class);
    assertFalse(getDeactivatedTenantData.active);

    final Response patchTenantActivate = this.patchTenantActivateRequestResponse(createdTenantData.tenantId);
    assertEquals(Response.Ok, patchTenantActivate.status);
    final TenantData activatedTenantData = deserialized(patchTenantActivate.entity.content, TenantData.class);
    assertTrue(activatedTenantData.active);

    final Response getTenantResponse2 = getTenantRequestResponse(location);
    assertEquals(Response.Ok, getTenantResponse2.status);
    final TenantData getActivatedTenantData = deserialized(getTenantResponse2.entity.content, TenantData.class);
    assertTrue(getActivatedTenantData.active);
  }

  @Test
  public void testThatTenantChangesDescription() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content, TenantData.class);
    
    final String location = createdResponse.headerOf(ResponseHeader.Location).value;
    
    final Response patchTenantDescription = this.patchTenantDescriptionRequestResponse(createdTenantData.tenantId, "New-Description");
    assertEquals(Response.Ok, patchTenantDescription.status);
    final TenantData redescribedTenantData = deserialized(patchTenantDescription.entity.content, TenantData.class);
    assertEquals("New-Description", redescribedTenantData.description);

    final Response getTenantResponse1 = getTenantRequestResponse(location);
    assertEquals(Response.Ok, getTenantResponse1.status);
    final TenantData getRenamedTenantData = deserialized(getTenantResponse1.entity.content, TenantData.class);
    assertEquals("New-Description", getRenamedTenantData.description);
  }

  @Test
  public void testThatTenantChangesName() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content, TenantData.class);
    
    final String location = createdResponse.headerOf(ResponseHeader.Location).value;
    
    final Response patchTenantName = this.patchTenantNameRequestResponse(createdTenantData.tenantId, "New-Name");
    assertEquals(Response.Ok, patchTenantName.status);
    final TenantData renamedTenantData = deserialized(patchTenantName.entity.content, TenantData.class);
    assertEquals("New-Name", renamedTenantData.name);

    final Response getTenantResponse1 = getTenantRequestResponse(location);
    assertEquals(Response.Ok, getTenantResponse1.status);
    final TenantData getRenamedTenantData = deserialized(getTenantResponse1.entity.content, TenantData.class);
    assertEquals("New-Name", getRenamedTenantData.name);
  }

  @Test
  public void testThatTenantGroupProvisions() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content, TenantData.class);
    
    final Response groupProvisionedResponse = postProvisionGroup(createdTenantData.tenantId, "Group", "A group description.");
    assertEquals(Response.Created, groupProvisionedResponse.status);
    final GroupData groupData = deserialized(groupProvisionedResponse.entity.content, GroupData.class);
    assertEquals(createdTenantData.tenantId, groupData.tenantId);
    assertEquals("Group", groupData.name);
    assertEquals("A group description.", groupData.description);
  }

  @Test
  public void testThatTenantRoleProvisions() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content, TenantData.class);
    
    final Response roleProvisionedResponse = postProvisionRole(createdTenantData.tenantId, "Role", "A role description.");
    assertEquals(Response.Created, roleProvisionedResponse.status);
    final RoleData roleData = deserialized(roleProvisionedResponse.entity.content, RoleData.class);
    assertEquals(createdTenantData.tenantId, roleData.tenantId);
    assertEquals("Role", roleData.name);
    assertEquals("A role description.", roleData.description);
  }

  @Test
  public void testThatTenantUserRegisters() {
    final TenantData tenantData = tenantData();

    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData);
    assertEquals(Response.Created, createdResponse.status);
    final TenantData createdTenantData = deserialized(createdResponse.entity.content, TenantData.class);
    
    final UserRegistrationData userRegData = userRegistrationData(createdTenantData.tenantId);
    final Response registerUserResponse = postRegisterUser(createdTenantData.tenantId, userRegData);
    assertEquals(Response.Created, registerUserResponse.status);
    final UserRegistrationData userRegistrateredData = deserialized(registerUserResponse.entity.content, UserRegistrationData.class);
    assertEquals(createdTenantData.tenantId, userRegistrateredData.tenantId);
    assertEquals(userRegData.username, userRegistrateredData.username);
    assertEquals(userRegData.profile.name.given, userRegistrateredData.profile.name.given);
    assertEquals(userRegData.profile.name.second, userRegistrateredData.profile.name.second);
    assertEquals(userRegData.profile.name.family, userRegistrateredData.profile.name.family);
    assertEquals(userRegData.profile.emailAddress, userRegistrateredData.profile.emailAddress);
    assertEquals(userRegData.profile.phone, userRegistrateredData.profile.phone);
    assertEquals(userRegData.credential.authority, userRegistrateredData.credential.authority);
    assertEquals(userRegData.credential.id, userRegistrateredData.credential.id);
    assertNotEquals(userRegData.credential.secret, userRegistrateredData.credential.secret);
    assertEquals("VLINGO", userRegistrateredData.credential.type);
    assertTrue(userRegistrateredData.active);
  }

  protected Properties resourceProperties() {
    return TestProperties.tenantResourceProperties();
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
