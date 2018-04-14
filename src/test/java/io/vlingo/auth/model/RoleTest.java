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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RoleTest {

  @Test
  public void testThatRoleDescriptionChanges() {
    final Role role = role();
    assertEquals("A test role.", role.description());
    role.changeDescription("A test role changed.");
    assertEquals("A test role changed.", role.description());
  }

  @Test
  public void testThatRoleGroupAssigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Role role = role(tenant);
    repository.add(role);
    role.assign(group);
    assertTrue(role.isInRole(group, repository));
  }

  @Test
  public void testThatRoleNestedGroupAssigned() {
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
  public void testThatRoleUserAssigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Role role = role(tenant);
    repository.add(role);
    final User user = user(tenant);
    role.assign(user);
    assertTrue(role.isInRole(user, repository));
    assertTrue(user.isInRole(role, repository));
  }

  @Test
  public void testThatRoleUserInNestedGroupAssigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Group nested = group(tenant, "Nested", "The nested group.");
    repository.add(nested);
    group.assign(nested);
    final User user = user(tenant);
    final Role role = role(tenant);
    repository.add(role);
    role.assign(user);
    assertTrue(role.isInRole(user, repository));
    assertTrue(user.isInRole(role, repository));
  }
}
