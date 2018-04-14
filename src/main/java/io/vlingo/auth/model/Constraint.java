// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

public final class Constraint {
  public final String description;
  public final String name;
  public final String value;

  public static Constraint of(final String name, final String value, final String description) {
    return new Constraint(name, value, description);
  }

  public Constraint(final String name, final String value, final String description) {
    this.name = name;
    this.value = value;
    this.description = description;
  }
}
