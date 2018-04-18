// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.auth.infra.resource.TestResponseChannelConsumer.Progress;
import io.vlingo.auth.model.Tenant;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import static io.vlingo.http.resource.serialization.JsonSerialization.deserialized;
import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;
import io.vlingo.wire.channel.ResponseChannelConsumer;
import io.vlingo.wire.fdx.bidirectional.ClientRequestResponseChannel;
import io.vlingo.wire.message.ByteBufferAllocator;
import io.vlingo.wire.message.Converters;
import io.vlingo.wire.node.Address;
import io.vlingo.wire.node.AddressType;
import io.vlingo.wire.node.Host;

public class TenantResourceTest {
  private ClientRequestResponseChannel client;
  private ResponseChannelConsumer consumer;
  private Progress progress;
  protected Resource<?> resource;
  protected Class<? extends ResourceHandler> resourceHandlerClass;
  protected Resources resources;
  private Server server;
  protected World world;

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
    
    final Response patchTenantDeactive = this.patchTenantDeactivateRequestResponse(createdTenantData.tenantId);
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

  @Before
  public void setUp() throws Exception {
    world = World.start("tenant-resource-test");

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
    
    server = Server.startWith(world.stage(), properties);
    Thread.sleep(10); // delay for server startup

    progress = new Progress();

    consumer = world.actorFor(Definition.has(TestResponseChannelConsumer.class, Definition.parameters(progress)), ResponseChannelConsumer.class);

    client = new ClientRequestResponseChannel(Address.from(Host.of("localhost"), 8080, AddressType.NONE), consumer, 100, 10240, world.defaultLogger());
  }

  @After
  public void tearDown() {
    client.close();

    server.stop();

    world.terminate();
  }

  private final ByteBuffer buffer = ByteBufferAllocator.allocate(65535);
  
  protected ByteBuffer toByteBuffer(final String requestContent) {
    buffer.clear();
    buffer.put(Converters.textToBytes(requestContent));
    buffer.flip();
    return buffer;
  }

  protected String patchActivateRequest(final String tenantId) {
    return "PATCH /tenants/" + tenantId + "/activate HTTP/1.1\nHost: vlingo.io\n\n";
  }

  protected String patchDeactivateRequest(final String tenantId) {
    return "PATCH /tenants/" + tenantId + "/deactivate HTTP/1.1\nHost: vlingo.io\n\n";
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

  protected TenantData tenantData(final Tenant tenant, final boolean withId) {
    return TenantData.from(withId ? tenant.tenantId().value : null, tenant.name(), tenant.description(), tenant.isActive());
  }

  protected UserRegistrationData userRegistrationData(final String tenantId) {
    return UserRegistrationData.from(
            tenantId,
            "useroftheyear",
            ProfileData.from(PersonNameData.of("Given", "A", "Family"), "me@family.us", "212-555-1212"),
            CredentialData.from("vlingo-platform", "useroftheyear", "topsecret"),
            true);
  }

  protected Response getTenantRequestResponse(final String tenantLocation) {
    final String request = "GET " + tenantLocation + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response patchTenantActivateRequestResponse(final String tenantId) {
    final String request = "PATCH /tenants/" + tenantId + "/activate HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response patchTenantDeactivateRequestResponse(final String tenantId) {
    final String request = "PATCH /tenants/" + tenantId + "/deactivate HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response patchTenantDescriptionRequestResponse(final String tenantId, final String description) {
    final String request = "PATCH /tenants/" + tenantId + "/description HTTP/1.1\nHost: vlingo.io\nContent-Length: " + description.length() + "\n\n" + description;
    return requestResponse(request);
  }

  protected Response patchTenantNameRequestResponse(final String tenantId, final String name) {
    final String request = "PATCH /tenants/" + tenantId + "/name HTTP/1.1\nHost: vlingo.io\nContent-Length: " + name.length() + "\n\n" + name;
    return requestResponse(request);
  }

  protected Response postProvisionGroup(final String tenantId, final String name, final String description) {
    final String body = serialized(GroupData.from(name, description));
    final String request = "POST /tenants/" + tenantId + "/groups HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
    return requestResponse(request);
  }

  protected Response postProvisionRole(final String tenantId, final String name, final String description) {
    final String body = serialized(RoleData.from(name, description));
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
    progress.untilConsumed = TestUntil.happenings(1);
    client.requestWith(toByteBuffer(request));
    while (progress.untilConsumed.remaining() > 0) {
      client.probeChannel();
    }
    progress.untilConsumed.completes();
    return progress.responses.poll();
  }
}
