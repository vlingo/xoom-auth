// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

public class EmailAddress {
  public final String value;
  
  public static EmailAddress of(final String value) {
    return new EmailAddress(value);
  }

  public EmailAddress(final String value) {
    if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("EmailAddress required.");
    this.value = value;
  }
}
