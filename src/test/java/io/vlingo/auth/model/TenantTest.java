// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TenantTest {

  @Test
  public void testThatTenantIsCreated() {
    final Tenant tenant = Tenant.subscribeFor("Test", "A test tenant.", true);
    assertNotNull(tenant);
    assertTrue(tenant.isActive());
    assertEquals("Test", tenant.name());
    assertEquals("A test tenant.", tenant.description());
  }

  @Test
  public void testThatTenantDeactivatesActivates() {
    final Tenant tenant = Tenant.subscribeFor("Test", "A test tenant.", true);
    assertTrue(tenant.isActive());
    tenant.deactivate();
    assertFalse(tenant.isActive());
    tenant.activate();
    assertTrue(tenant.isActive());
  }

  @Test
  public void testThatDescriptionChanges() {
    final Tenant tenant = Tenant.subscribeFor("Test", "A test tenant.", true);
    assertEquals("A test tenant.", tenant.description());
    tenant.changeDescription("A test tenant re-described.");
    assertEquals("A test tenant re-described.", tenant.description());
  }

  @Test
  public void testThatNameChanges() {
    final Tenant tenant = Tenant.subscribeFor("Test", "A test tenant.", true);
    assertEquals("Test", tenant.name());
    tenant.changeName("Test-Renamed");
    assertEquals("Test-Renamed", tenant.name());
  }

  @Test
  public void testThatTenantProvisionsGroup() {
    final Tenant tenant = Tenant.subscribeFor("Test", "A test tenant.", true);
    final Group group = tenant.provisionGroup("Test", "A test group.");
    assertNotNull(group);
    assertEquals("Test", group.name());
    assertEquals("A test group.", group.description());
  }

  @Test
  public void testThatTenantProvisionsRole() {
    final Tenant tenant = Tenant.subscribeFor("Test", "A test tenant.", true);
    final Role role = tenant.provisionRole("Test", "A test role.");
    assertNotNull(role);
    assertEquals("Test", role.name());
    assertEquals("A test role.", role.description());
  }

  @Test
  public void testThatUserRegisters() {
    final Tenant tenant = Tenant.subscribeFor("Test", "A test tenant.", true);
    final User user =
            tenant.registerUser(
                    "test",
                    Profile.with(PersonName.of("Given", "A", "Family"), EmailAddress.of("given@family.org"), Phone.of("303-555-1212")),
                    Credential.vlingoCredentialFrom("vlingo-platform", "given@family.org", "bigsecret"),
                    true);

    assertNotNull(user);

    assertNotNull(user.profile());
    assertEquals("Given", user.profile().name.given);
    assertEquals("A", user.profile().name.second);
    assertEquals("Family", user.profile().name.family);
    assertEquals("given@family.org", user.profile().emailAddress.value);
    assertEquals("303-555-1212", user.profile().phone.value);

    assertNotNull(user.credentials());
    assertEquals(1, user.credentials().size());
    assertEquals("vlingo-platform", user.credentials().iterator().next().authority);
    assertEquals("given@family.org", user.credentials().iterator().next().id);
    assertEquals("bigsecret", user.credentials().iterator().next().secret);
    assertTrue(user.credentials().iterator().next().isVlingo());

    assertTrue(user.isActive());
  }
}
