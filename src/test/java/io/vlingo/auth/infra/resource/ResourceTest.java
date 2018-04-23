// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

import java.nio.ByteBuffer;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.auth.infra.resource.TestResponseChannelConsumer.Progress;
import io.vlingo.auth.model.Tenant;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Server;
import io.vlingo.wire.channel.ResponseChannelConsumer;
import io.vlingo.wire.fdx.bidirectional.ClientRequestResponseChannel;
import io.vlingo.wire.message.ByteBufferAllocator;
import io.vlingo.wire.message.Converters;
import io.vlingo.wire.node.Address;
import io.vlingo.wire.node.AddressType;
import io.vlingo.wire.node.Host;

public abstract class ResourceTest {
  private final ByteBuffer buffer = ByteBufferAllocator.allocate(65535);

  protected ResponseChannelConsumer consumer;
  protected ClientRequestResponseChannel client;
  protected Progress progress;
  protected Server server;
  protected World world;

  @Before
  public void setUp() throws Exception {
    world = World.start("resource-test");
    
    server = Server.startWith(world.stage(), resourceProperties());
    Thread.sleep(10); // delay for server startup

    consumer = world.actorFor(Definition.has(TestResponseChannelConsumer.class, Definition.parameters(progress)), ResponseChannelConsumer.class);

    client = new ClientRequestResponseChannel(Address.from(Host.of("localhost"), 8080, AddressType.NONE), consumer, 100, 10240, world.defaultLogger());
  }

  @After
  public void tearDown() {
    client.close();

    server.stop();

    world.terminate();
  }

  protected ResourceTest() {
    progress = new Progress();
  }

  protected abstract Properties resourceProperties();

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

  protected Response getRoleRequestResponse(String tenantId, String name) {
    final String request = "GET /tenants/" + tenantId + "/roles/" + name + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getRoleGroupRequestResponse(RoleData roleData, String groupName) {
    final String request = "GET /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/groups/" + groupName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  protected Response getRoleUserRequestResponse(RoleData roleData, String username) {
    final String request = "GET /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/users/" + username + " HTTP/1.1\nHost: vlingo.io\n\n";
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
