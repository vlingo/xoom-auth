// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infrastructure.resource;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.http.Header;
import io.vlingo.xoom.http.Request;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.wire.message.ByteBufferAllocator;
import io.vlingo.xoom.wire.message.Converters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;

public abstract class ResourceTest {
  private static final AtomicInteger baseServerPort = new AtomicInteger(19090);

  private final ByteBuffer buffer = ByteBufferAllocator.allocate(65535);

  protected XoomInitializer xoom;
  protected int serverPort;

  @BeforeEach
  public void setUp() throws Exception {
    serverPort = baseServerPort.getAndIncrement();

    XoomInitializer.main(new String[]{"-Dport=" + serverPort});
    xoom = XoomInitializer.instance();
    xoom.server().startUp().await(100);
  }

  @AfterEach
  public void tearDown() throws Exception {
    xoom.stopServer();
    xoom.terminateWorld();
    ComponentRegistry.clear();
  }

  protected ByteBuffer toByteBuffer(final String requestContent) {
    buffer.clear();
    buffer.put(Converters.textToBytes(requestContent));
    buffer.flip();
    return buffer;
  }

  protected String postSubscribeRequest(final String body) {
    return "POST /tenants HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
  }

  protected String tenant() {
    return serialized(tenantData());
  }

  protected TenantData tenantData() {
    return tenantData("Test-Tenant", "Test-Tenant description.", true);
  }

  protected TenantData tenantData(final String name, final String description) {
    return tenantData(name, description, true);
  }

  protected TenantData tenantData(final String name, final String description, boolean active) {
    return TenantData.from(name, description, active);
  }

//  protected TenantData tenantData(final Tenant tenant, final boolean withId) {
//    return TenantData.from(withId ? tenant.tenantId().value : null, tenant.name(), tenant.description(), tenant.isActive());
//  }

  protected UserRegistrationData userRegistrationData(final String tenantId) {
    return UserRegistrationData.from(
            tenantId,
            "useroftheyear",
            ProfileData.from(PersonNameData.of("Given", "A", "Family"), "me@family.us", "212-555-1212"),
            CredentialData.from("xoom-platform", "useroftheyear", "topsecret"),
            true);
  }

  protected UserRegistrationData userRegistrationData(final String tenantId, final int value) {
    return UserRegistrationData.from(
            tenantId,
            "useroftheyear" + value,
            ProfileData.from(PersonNameData.of("Given" + value, "A", "Family" + value), "me" + value + "@family.us", "212-555-1212"),
            CredentialData.from("xoom-platform", "useroftheyear" + value, "topsecret" + value),
            true);
  }

  protected Response getTenantRequestResponse(final String tenantLocation) {
    final String request = "GET " + tenantLocation + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getTenantGroupsRequestResponse(String tenantLocation) {
    final String request = "GET " + tenantLocation + "/groups HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getTenantPermissionsRequestResponse(String tenantLocation) {
    final String request = "GET " + tenantLocation + "/permissions HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getTenantRolesRequestResponse(final String tenantLocation) {
    final String request = "GET " + tenantLocation + "/roles HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getTenantUsersRequestResponse(final String tenantLocation) {
    final String request = "GET " + tenantLocation + "/users HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getGroupRequestResponse(GroupData groupData) {
    final String request = "GET /tenants/" + groupData.tenantId + "/groups/" + groupData.name + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getGroupGroupRequestResponse(GroupData groupData, String groupName) {
    final String request = "GET /tenants/" + groupData.tenantId + "/groups/" + groupData.name + "/groups/" + groupName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getGroupPermissionRequestResponse(GroupData groupData, String permissionName) {
    final String request = "GET /tenants/" + groupData.tenantId + "/groups/" + groupData.name + "/permissions/" + permissionName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getGroupRoleRequestResponse(GroupData groupData, String roleName) {
    final String request = "GET /tenants/" + groupData.tenantId + "/groups/" + groupData.name + "/roles/" + roleName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getGroupUserRequestResponse(GroupData groupData, String username) {
    final String request = "GET /tenants/" + groupData.tenantId + "/groups/" + groupData.name + "/users/" + username + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getRoleRequestResponse(String tenantId, String name) {
    final String request = "GET /tenants/" + tenantId + "/roles/" + name + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getRoleGroupRequestResponse(RoleData roleData, String groupName) {
    final String request = "GET /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/groups/" + groupName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getRolePermissionRequestResponse(RoleData roleData, String permissionName) {
    final String request = "GET /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/permissions/" + permissionName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getRoleUserRequestResponse(RoleData roleData, String username) {
    final String request = "GET /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/users/" + username + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response postProvisionGroup(final String tenantId, final String name, final String description) {
    final String body = serialized(GroupData.from(null, name, description, tenantId));
    final String request = "POST /tenants/" + tenantId + "/groups HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
    return requestResponse(request);
  }

  protected Response postProvisionPermission(String tenantId, String name, String description) {
    final String body = serialized(PermissionData.from(null, Collections.emptySet(), name, description, tenantId));
    final String request = "POST /tenants/" + tenantId + "/permissions HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
    return requestResponse(request);
  }

  protected Response postProvisionRole(final String tenantId, final String name, final String description) {
    final String body = serialized(RoleData.from(null, tenantId, name, description));
    final String request = "POST /tenants/" + tenantId + "/roles HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
    return requestResponse(request);
  }

  protected Response postTenantSubscribesRequestResponse(final TenantData tenantToSubscribe) {
    final String request = postSubscribeRequest(serialized(tenantToSubscribe));
    return requestResponse(request);
  }

  protected Response postRegisterUser(String tenantId, UserRegistrationData userRegData) {
    final String body = serialized(userRegData);
    final String request = "POST /tenants/" + tenantId + "/users HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
    return requestResponse(request);
  }

  protected Response requestResponse(final String request) {
    return requestResponse(Request.from(toByteBuffer(request)));
  }

  private Response requestResponse(Request request) {
    final io.restassured.response.Response response = given()
            .port(serverPort)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .filter(new RequestLoggingFilter())
            .filter(new ResponseLoggingFilter())
            .when()
            .headers(request.headers.stream().filter(h -> !h.name.equals("Content-Length")).collect(Collectors.toMap(h -> h.name, h -> h.value)))
            .body(request.body.content())
            .request(request.method.name, request.uri.getPath())
            .andReturn();
    final Stream<ResponseHeader> headers = response.headers().asList().stream().map(h -> ResponseHeader.of(h.getName(), h.getValue()));
    final Response.Status statusCode = Response.Status.valueOfRawState(response.statusLine().replaceFirst("HTTP/1.1 ", ""));
    return Response.of(
            statusCode,
            Header.Headers.of(headers.toArray(ResponseHeader[]::new)),
            response.body().asString()
    );
  }
}
