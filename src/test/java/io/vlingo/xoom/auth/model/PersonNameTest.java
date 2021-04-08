// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class PersonNameTest {

  @Test
  public void testThatNameChanges() {
    final PersonName name = PersonName.of("Given", "A", "Family");
    assertEquals("Given", name.given);
    assertEquals("A", name.second);
    assertEquals("Family", name.family);

    final PersonName nowJohn = name.withGivenOf("John");
    assertNotEquals(name, nowJohn);
    assertEquals("John", nowJohn.given);
    assertEquals("A", nowJohn.second);
    assertEquals("Family", nowJohn.family);

    final PersonName nowWayne = name.withSecondOf("Wayne");
    assertNotEquals(name, nowWayne);
    assertEquals("Given", nowWayne.given);
    assertEquals("Wayne", nowWayne.second);
    assertEquals("Family", nowWayne.family);

    final PersonName nowDoe = name.withFamilyOf("Doe");
    assertNotEquals(name, nowDoe);
    assertEquals("Given", nowDoe.given);
    assertEquals("A", nowDoe.second);
    assertEquals("Doe", nowDoe.family);
    
    final PersonName changed = name.withGivenOf("John").withSecondOf("Wayne").withFamilyOf("Doe");
    assertNotEquals(name, changed);
    assertEquals("John", changed.given);
    assertEquals("Wayne", changed.second);
    assertEquals("Doe", changed.family);
  }
}
