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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class UserTest {

  @Test
  public void testThatUserDeactivatesActivates() {
    final User user = user();
    assertNotNull(user);
    assertTrue(user.isActive());
    user.deactivate();
    assertFalse(user.isActive());
    user.activate();
    assertTrue(user.isActive());
  }

  @Test
  public void testThatCredentialIsAdded() {
    final User user = user();
    assertEquals(1, user.credentials().size());
    user.add(Credential.oauthCredentialFrom("partner-platform", "me@about.org", "reallybigsecret"));
    assertEquals(2, user.credentials().size());
    
    final Set<Credential> sorted = new TreeSet<>();
    sorted.addAll(user.credentials());
    final Iterator<Credential> iterator = sorted.iterator();
    final Credential credential1 = iterator.next();
    final Credential credential2 = iterator.next();
    
    assertEquals("partner-platform", credential1.authority);
    assertEquals("me@about.org", credential1.id);
    assertEquals("reallybigsecret", credential1.secret);
    assertTrue(credential1.isOauth());
    
    assertEquals("vlingo-platform", credential2.authority);
    assertEquals("given@family.org", credential2.id);
    assertEquals("bigsecret", credential2.secret);
    assertTrue(credential2.isVlingo());
  }

  @Test
  public void testThatCredentialIsRemoved() {
    final User user = user();
    assertEquals(1, user.credentials().size());
    user.add(Credential.oauthCredentialFrom("partner-platform", "me@about.org", "reallybigsecret"));
    assertEquals(2, user.credentials().size());
    
    user.remove(user.credentials().iterator().next());
    assertEquals(1, user.credentials().size());
    user.remove(user.credentials().iterator().next());
    assertEquals(0, user.credentials().size());
  }

  @Test
  public void testThatCredentialIsReplaced() {
    final User user = user();
    assertEquals(1, user.credentials().size());

    final Credential replacement = Credential.oauthCredentialFrom("partner-platform", "me@about.org", "reallybigsecret");

    user.replace(user.credentials().iterator().next(), replacement);
    assertEquals(1, user.credentials().size());
    assertEquals(replacement, user.credentials().iterator().next());
  }

  @Test
  public void testThatProfileChanges() {
    final User user = user();
    final Profile replacement = Profile.with(PersonName.of("Joe", "Wayne", "Doe"), EmailAddress.of("john@doe.org"), Phone.of("123-456-7890"));
    user.replace(replacement);
    
    assertNotNull(user.profile());
    assertEquals("Joe", user.profile().name.given);
    assertEquals("Wayne", user.profile().name.second);
    assertEquals("Doe", user.profile().name.family);
    assertEquals("john@doe.org", user.profile().emailAddress.value);
    assertEquals("123-456-7890", user.profile().phone.value);
  }

  @Test
  public void testUserIsInRole() {
    final Repository repository = new Repository();
    final User user = user();
    final Role role = role();
    repository.add(role);
    role.assign(user);
    
    assertTrue(user.isInRole(role, repository));
    assertTrue(role.isInRole(user, repository));
  }

  @Test
  public void testUserIsInRoleByGroup() {
    final Repository repository = new Repository();
    final Tenant tenant = tenant();
    final User user = user(tenant);
    final Group group = group(tenant);
    repository.add(group);
    final Group innerGroup = group(tenant, "Inner", "Inner description.");
    repository.add(innerGroup);
    group.assign(innerGroup);
    innerGroup.assign(user);
    final Role role = role(tenant);
    repository.add(role);
    role.assign(group);
    
    assertTrue(user.isInRole(role, repository));
    assertTrue(role.isInRole(user, repository));
  }

  @Test
  public void testThatTenantSame() {
    final Tenant tenant = tenant();
    assertEquals(tenant.tenantId(), user(tenant).tenantId());
  }
}
