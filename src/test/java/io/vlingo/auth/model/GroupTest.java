// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import static io.vlingo.auth.model.ModelFixtures.group;
import static io.vlingo.auth.model.ModelFixtures.role;
import static io.vlingo.auth.model.ModelFixtures.tenant;
import static io.vlingo.auth.model.ModelFixtures.user;

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
    assertTrue(group.isMember(another, repository));
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
    assertTrue(group.isMember(another, repository));
    group.unassign(another);
    assertFalse(group.isMember(another, repository));
  }

  @Test
  public void testThatGroupUserMemberAssigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final User user = user(tenant);
    group.assign(user);
    assertTrue(group.isMember(user, repository));
  }

  @Test
  public void testThatGroupUserMemberUnassigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final User user = user(tenant);
    group.assign(user);
    assertTrue(group.isMember(user, repository));
    group.unassign(user);
    assertFalse(group.isMember(user, repository));
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
    assertTrue(group.isMember(another, repository));
    assertTrue(group.isMember(user, repository));
    assertTrue(another.isMember(user, repository));
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
    assertTrue(group.isMember(another, repository));
    assertTrue(group.isMember(yetAnother, repository));
    assertTrue(another.isMember(yetAnother, repository));
    assertTrue(group.isMember(user, repository));
    assertTrue(another.isMember(user, repository));
    assertTrue(yetAnother.isMember(user, repository));
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
  }

  @Test
  public void testThatTenantSame() {
    final Tenant tenant = tenant();
    assertEquals(tenant.tenantId(), group(tenant).tenantId());
  }
}
