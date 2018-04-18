// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

public final class PersonNameData {
  public final String given;
  public final String family;
  public final String second;

  public static PersonNameData of(final String given, final String second, final String family) {
    return new PersonNameData(given, second, family);
  }

  public PersonNameData(final String given, final String second, final String family) {
    this.given = given;
    this.second = second;
    this.family = family;
  }
}
