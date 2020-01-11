// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import static io.vlingo.common.serialization.JsonSerialization.deserialized;
import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

import io.vlingo.http.Response;

public class PermissionResourceTest extends ResourceTest {
  private PermissionData permissionData;
  private TenantData tenantData;

  @Test
  public void testThatPermissionDescriptionChanges() {
    permission();

    final Response patchPermissionChangeDescription = patchPermissionChangeDescriptionRequestResponse(permissionData, "Changed permission description.");
    assertEquals(Response.Status.Ok, patchPermissionChangeDescription.status);
    final PermissionData changedPermissionData = deserialized(patchPermissionChangeDescription.entity.content(), PermissionData.class);
    assertEquals("Changed permission description.", changedPermissionData.description);
  }

  @Test
  public void testThatPermissionEnforcesConstraint() {
    permission();

    final ConstraintData constraintData1 = constraintData(1);
    final Response patchPermissionEnforceConstraint = patchPermissionEnforceConstraintRequestResponse(permissionData, constraintData1);
    assertEquals(Response.Status.Ok, patchPermissionEnforceConstraint.status);
    final PermissionData changedPermissionData = deserialized(patchPermissionEnforceConstraint.entity.content(), PermissionData.class);
    assertEquals(1, changedPermissionData.constraints.size());
    final ConstraintData constraintDataEnforced = changedPermissionData.constraints.iterator().next();
    assertEquals(constraintData1.type, constraintDataEnforced.type);
    assertEquals(constraintData1.name, constraintDataEnforced.name);
    assertEquals(constraintData1.value, constraintDataEnforced.value);
    assertEquals(constraintData1.description, constraintDataEnforced.description);
  }

  @Test
  public void testThatPermissionEnforcesReplacementConstraint() {
    permission();

    final ConstraintData constraintData1 = constraintData(1);
    final Response patchPermissionEnforceConstraint = patchPermissionEnforceConstraintRequestResponse(permissionData, constraintData1);
    assertEquals(Response.Status.Ok, patchPermissionEnforceConstraint.status);
    final ConstraintData constraintData2 = constraintData(2);
    final Response patchPermissionEnforceReplacementConstraint = patchPermissionEnforceConstraintRequestResponse(permissionData, constraintData1, constraintData2);
    final PermissionData changedPermissionData = deserialized(patchPermissionEnforceReplacementConstraint.entity.content(), PermissionData.class);
    assertEquals(1, changedPermissionData.constraints.size());
    final ConstraintData constraintDataEnforced = changedPermissionData.constraints.iterator().next();
    assertEquals(constraintData2.type, constraintDataEnforced.type);
    assertEquals(constraintData2.name, constraintDataEnforced.name);
    assertEquals(constraintData2.value, constraintDataEnforced.value);
    assertEquals(constraintData2.description, constraintDataEnforced.description);
  }

  @Test
  public void testThatPermissionForgetsConstraint() {
    permission();

    final ConstraintData constraintData1 = constraintData(1);
    final Response patchPermissionEnforceConstraint1 = patchPermissionEnforceConstraintRequestResponse(permissionData, constraintData1);
    assertEquals(Response.Status.Ok, patchPermissionEnforceConstraint1.status);
    final ConstraintData constraintData2 = constraintData(2);
    final Response patchPermissionEnforceConstraint2 = patchPermissionEnforceConstraintRequestResponse(permissionData, constraintData2);
    assertEquals(Response.Status.Ok, patchPermissionEnforceConstraint2.status);
    final PermissionData changedPermissionData = deserialized(patchPermissionEnforceConstraint2.entity.content(), PermissionData.class);
    assertEquals(2, changedPermissionData.constraints.size());
    final Response patchPermissionForgetConstraint = patchPermissionForgetConstraintRequestResponse(permissionData, constraintData1);
    final PermissionData constraintForgottenPermissionData = deserialized(patchPermissionForgetConstraint.entity.content(), PermissionData.class);
    assertEquals(1, constraintForgottenPermissionData.constraints.size());
    final ConstraintData constraintDataForgotten = constraintForgottenPermissionData.constraints.iterator().next();
    assertEquals(constraintData2.type, constraintDataForgotten.type);
    assertEquals(constraintData2.name, constraintDataForgotten.name);
    assertEquals(constraintData2.value, constraintDataForgotten.value);
    assertEquals(constraintData2.description, constraintDataForgotten.description);
  }

  @Test
  public void testThatPermissionQueryFindsMultiple() {
    permission();
    final PermissionData permissionData1 = permissionData;
    permission(permissionData1.tenantId, 2);
    final PermissionData permissionData2 = permissionData;
    final Response queryResponse1 = getPermissionRequestResponse(permissionData1.tenantId, permissionData1.name);
    final PermissionData queriedPermissionData1 = deserialized(queryResponse1.entity.content(), PermissionData.class);
    final Response queryResponse2 = getPermissionRequestResponse(permissionData2.tenantId, permissionData2.name);
    final PermissionData queriedPermissionData2 = deserialized(queryResponse2.entity.content(), PermissionData.class);
    assertEquals(permissionData1.name, queriedPermissionData1.name);
    assertEquals(permissionData1.description, queriedPermissionData1.description);
    assertTrue(permissionData1.constraints.isEmpty());
    assertEquals(permissionData2.name, queriedPermissionData2.name);
    assertEquals(permissionData2.description, queriedPermissionData2.description);
    assertTrue(permissionData2.constraints.isEmpty());
  }

  @Override
  protected Properties resourceProperties() {
    return TestProperties.permissionResourceProperties(TestProperties.tenantResourceProperties());
  }

  private ConstraintData constraintData(final int value) {
    return ConstraintData.from("String", "Name" + value, "" + value, "Description " + value + ".");
  }

  private Response getPermissionRequestResponse(final String tenantId, final String name) {
    final String request = "GET /tenants/" + permissionData.tenantId + "/permissions/" + name + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response patchPermissionChangeDescriptionRequestResponse(final PermissionData permissionData, final String description) {
    final String request = "PATCH /tenants/" + permissionData.tenantId + "/permissions/" + permissionData.name + "/description HTTP/1.1\nHost: vlingo.io\nContent-Length: " + description.length() + "\n\n" + description;
    return requestResponse(request);
  }

  private Response patchPermissionEnforceConstraintRequestResponse(final PermissionData permissionData, final ConstraintData constraintData) {
    final String body = serialized(constraintData);
    final String request = "PATCH /tenants/" + permissionData.tenantId + "/permissions/" + permissionData.name + "/constraints HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
    return requestResponse(request);
  }

  private Response patchPermissionEnforceConstraintRequestResponse(final PermissionData permissionData, final ConstraintData previousConstraintData, final ConstraintData currentConstraintData) {
    final String body = serialized(currentConstraintData);
    final String request = "PATCH /tenants/" + permissionData.tenantId + "/permissions/" + permissionData.name + "/constraints/" + previousConstraintData.name + " HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
    return requestResponse(request);
  }

  private Response patchPermissionForgetConstraintRequestResponse(final PermissionData permissionData, final ConstraintData constraintData) {
    final String request = "DELETE /tenants/" + permissionData.tenantId + "/permissions/" + permissionData.name + "/constraints/" + constraintData.name + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private void permission() {
    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData());
    tenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    permission(tenantData.tenantId, 1);
  }

  private void permission(final String tenantId, final int value) {
    final Response permissionResponse = postProvisionPermission(tenantData.tenantId, "Permission"+value, "A permission description " + value + ".");
    permissionData = deserialized(permissionResponse.entity.content(), PermissionData.class);
  }
}
