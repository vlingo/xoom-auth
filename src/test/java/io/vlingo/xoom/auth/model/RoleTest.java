// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.vlingo.xoom.auth.model.Constraint.Type;

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
  public void testThatRoleGroupUnassigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Group group = group(tenant);
    repository.add(group);
    final Role role = role(tenant);
    repository.add(role);
    role.assign(group);
    assertTrue(role.isInRole(group, repository));
    role.unassign(group);
    assertFalse(role.isInRole(group, repository));
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
  public void testThatRoleNestedGroupUnassigned() {
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
    role.unassign(group);
    assertFalse(role.isInRole(nested, repository));
    assertFalse(nested.isInRole(role, repository));
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
  public void testThatRoleUserUnassigned() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Role role = role(tenant);
    repository.add(role);
    final User user = user(tenant);
    role.assign(user);
    assertTrue(role.isInRole(user, repository));
    assertTrue(user.isInRole(role, repository));
    role.unassign(user);
    assertFalse(role.isInRole(user, repository));
    assertFalse(user.isInRole(role, repository));
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

  @Test
  public void testThatRoleUserInNestedGroupUnassigned() {
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
    role.unassign(user);
    assertFalse(role.isInRole(user, repository));
    assertFalse(user.isInRole(role, repository));
  }

  @Test
  public void testThatRolePermissionAttaches() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Role role = role(tenant);
    repository.add(role);
    final Permission permission = Permission.with(tenant.tenantId(), "Test", "A test permission.");
    repository.add(permission);
    role.attach(permission);
    assertNotNull(role.permissionOf(permission.name(), repository));
    assertEquals(1, role.permissions(repository).size());
  }

  @Test
  public void testThatRolePermissionDettaches() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Role role = role(tenant);
    repository.add(role);
    final Permission permission = Permission.with(tenant.tenantId(), "Test", "A test permission.");
    repository.add(permission);
    role.attach(permission);
    assertNotNull(role.permissionOf(permission.name(), repository));
    assertEquals(1, role.permissions(repository).size());
    role.detach(permission);
    assertNull(role.permissionOf(permission.name(), repository));
    assertTrue(role.permissions(repository).isEmpty());
  }

  @Test
  public void testThatRolePermissionEnforcesConstraint() {
    final Tenant tenant = tenant();
    final Repository repository = new Repository();
    final Role role = role(tenant);
    repository.add(role);
    final Permission permission = Permission.with(tenant.tenantId(), "Test", "A test permission.");
    repository.add(permission);
    role.attach(permission);
    final Constraint constraint = Constraint.of(Type.Integer, "LimitPerCustomer", "3", "A limit of 3 per customer.");
    role.permissionOf(permission.name(), repository).enforce(constraint);
    assertEquals("3", role.permissionOf(permission.name(), repository).constraintOf("LimitPerCustomer").value);
  }
}
