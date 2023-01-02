// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

import static io.vlingo.xoom.auth.model.ModelFixtures.group;
import static io.vlingo.xoom.auth.model.ModelFixtures.role;
import static io.vlingo.xoom.auth.model.ModelFixtures.tenant;
import static io.vlingo.xoom.auth.model.ModelFixtures.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GroupTest {

  @Test
  public void testThatGroupDescriptionChanges() {
    final Group group = group();
    assertEquals("A test group.", group.description());
    group.changeDescription("A test group changed.");
    assertEquals("A test group changed.", group.description());
  }

  @Test
  public void testThatGroupMemberGroupAssigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Group another = group(tenant, "Another", "Another test group.");
    repository.add(another);
    group.assign(another);
    assertTrue(group.hasMember(another, repository));
  }

  @Test
  public void testThatGroupMemberGroupUnassigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Group another = group(tenant, "Another", "Another test group.");
    repository.add(another);
    group.assign(another);
    assertTrue(group.hasMember(another, repository));
    group.unassign(another);
    assertFalse(group.hasMember(another, repository));
  }

  @Test
  public void testThatGroupUserMemberAssigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final User user = user(tenant);
    group.assign(user);
    assertTrue(group.hasMember(user, repository));
  }

  @Test
  public void testThatGroupUserMemberUnassigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final User user = user(tenant);
    group.assign(user);
    assertTrue(group.hasMember(user, repository));
    group.unassign(user);
    assertFalse(group.hasMember(user, repository));
  }

  @Test
  public void testThatNestedGroupUserMemberAssigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Group another = group(tenant, "Another", "Another test group.");
    repository.add(another);
    group.assign(another);
    final User user = user(tenant);
    another.assign(user);
    assertTrue(group.hasMember(another, repository));
    assertTrue(group.hasMember(user, repository));
    assertTrue(another.hasMember(user, repository));
  }

  @Test
  public void testThatTwoNestedGroupUserMemberAssigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Group another = group(tenant, "Another", "Another test group.");
    repository.add(another);
    group.assign(another);
    final Group yetAnother = group(tenant, "Yet Another", "Yet another test group.");
    repository.add(yetAnother);
    another.assign(yetAnother);
    final User user = user(tenant);
    yetAnother.assign(user);
    assertTrue(group.hasMember(another, repository));
    assertTrue(group.hasMember(yetAnother, repository));
    assertTrue(another.hasMember(yetAnother, repository));
    assertTrue(group.hasMember(user, repository));
    assertTrue(another.hasMember(user, repository));
    assertTrue(yetAnother.hasMember(user, repository));
  }

  @Test
  public void testThatGroupIsInRole() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Role role = role(tenant);
    role.assign(group);
    assertTrue(role.isInRole(group, repository));
    assertTrue(group.isInRole(role, repository));
  }

  @Test
  public void testThatNestedGroupIsInRole() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Group nested = group(tenant, "Nested", "The nested group.");
    repository.add(nested);
    group.assign(nested);
    final Role role = role(tenant);
    role.assign(group);
    assertTrue(role.isInRole(nested, repository));
    assertTrue(nested.isInRole(role, repository));
  }

  @Test
  public void testThatGroupHasRolePermission() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Role role = role(tenant);
    repository.add(role);
    final Permission permission = Permission.with(tenant.tenantId(), "Test", "A test permission.");
    repository.add(permission);
    role.attach(permission);
    role.assign(group);
    assertTrue(group.hasPermission(permission, repository));
  }

  @Test
  public void testThatNestedGroupHasRolePermission() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Group nested = group(tenant, "Nested", "The nested group.");
    repository.add(nested);
    group.assign(nested);
    final Role role = role(tenant);
    repository.add(role);
    final Permission permission = Permission.with(tenant.tenantId(), "Test", "A test permission.");
    repository.add(permission);
    role.attach(permission);
    role.assign(group);
    assertTrue(nested.hasPermission(permission, repository));
  }

  @Test
  public void testThatTenantSame() {
    final Tenant tenant = tenant();
    assertEquals(tenant.tenantId(), group(tenant).tenantId());
  }
}
