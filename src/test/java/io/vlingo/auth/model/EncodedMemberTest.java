// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.vlingo.auth.model.EncodedMember.GroupMember;
import io.vlingo.auth.model.EncodedMember.RoleMember;
import io.vlingo.auth.model.EncodedMember.UserMember;

public class EncodedMemberTest {

  @Test
  public void testGroupMember() {
    final GroupMember member = new GroupMember(Group.with(TenantId.unique(), "Test", "A test group."));
    
    assertEquals("Test", member.id);
    assertTrue(member.isGroup());
    assertTrue(member.isSameAs(GroupMember.GroupType));
    assertFalse(member.isRole());
    assertFalse(member.isUser());
  }

  @Test
  public void testRoleMember() {
    final RoleMember member = new RoleMember(Role.with(TenantId.unique(), "Test", "A test role."));
    
    assertEquals("Test", member.id);
    assertTrue(member.isRole());
    assertTrue(member.isSameAs(GroupMember.RoleType));
    assertFalse(member.isGroup());
    assertFalse(member.isUser());
  }

  @Test
  public void testUserMember() {
    final UserMember member = new UserMember(user());
    
    assertEquals("Test", member.id);
    assertTrue(member.isUser());
    assertTrue(member.isSameAs(GroupMember.UserType));
    assertFalse(member.isGroup());
    assertFalse(member.isRole());
  }

  private User user() {
    final Tenant tenant = Tenant.with("Test", "", true);

    final User user =
            tenant.registerUser(
                    "Test",
                    Profile.with(PersonName.of("Given", "A", "Family"), EmailAddress.of("given@family.org"), Phone.of("303-555-1212")),
                    Credential.vlingoCredentialFrom("vlingo-platform", "given@family.org", "bigsecret"),
                    true);
    return user;
  }
}
