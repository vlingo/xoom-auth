// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

public class Phone {
  public final String value;
  
  public static Phone of(final String value) {
    return new Phone(value);
  }

  public Phone(final String value) {
    if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Phone required.");
    this.value = value;
  }
}
