// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProfileTest {

  @Test
  public void testThatProfileChanges() {
    final Profile profile = Profile.with(PersonName.of("Given", "A", "Family"), EmailAddress.of("given@family.org"), Phone.of("303-555-1212"));
    assertEquals("Given", profile.name.given);
    assertEquals("A", profile.name.second);
    assertEquals("Family", profile.name.family);
    assertEquals("given@family.org", profile.emailAddress.value);
    assertEquals("303-555-1212", profile.phone.value);

    final Profile withName = profile.withNameOf(profile.name.withGivenOf("John").withSecondOf("Wayne").withFamilyOf("Doe"));
    assertEquals("John", withName.name.given);
    assertEquals("Wayne", withName.name.second);
    assertEquals("Doe", withName.name.family);
    assertEquals("given@family.org", withName.emailAddress.value);
    assertEquals("303-555-1212", withName.phone.value);

    final Profile withEmailAddress = withName.withEmailAddressOf(EmailAddress.of("john@doe.org"));
    assertEquals("John", withEmailAddress.name.given);
    assertEquals("Wayne", withEmailAddress.name.second);
    assertEquals("Doe", withEmailAddress.name.family);
    assertEquals("john@doe.org", withEmailAddress.emailAddress.value);
    assertEquals("303-555-1212", withEmailAddress.phone.value);

    final Profile withPhone = withEmailAddress.withPhoneOf(Phone.of("123-456-7890"));
    assertEquals("John", withPhone.name.given);
    assertEquals("Wayne", withPhone.name.second);
    assertEquals("Doe", withPhone.name.family);
    assertEquals("john@doe.org", withPhone.emailAddress.value);
    assertEquals("123-456-7890", withPhone.phone.value);
  }
}
