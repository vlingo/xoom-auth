// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

public final class PersonName {
  public final String given;
  public final String family;
  public final String second;

  public static PersonName of(final String given, final String second, final String family) {
    return new PersonName(given, second, family);
  }

  public PersonName withFamilyOf(final String family) {
    return new PersonName(this.given, this.second, family);
  }

  public PersonName withGivenOf(final String given) {
    return new PersonName(given, this.second, this.family);
  }

  public PersonName withSecondOf(final String second) {
    return new PersonName(this.given, second, this.family);
  }

  private PersonName(final String given, final String second, final String family) {
    if (given == null || given.trim().isEmpty()) throw new IllegalArgumentException("PersonName given required.");
    this.given = given;
    
    this.second = second;

    if (family == null || family.trim().isEmpty()) throw new IllegalArgumentException("PersonName family required.");
    this.family = family;
  }
}
