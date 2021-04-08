// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

public final class Profile {
  public final EmailAddress emailAddress;
  public final PersonName name;
  public final Phone phone;

  public static Profile with(final PersonName name, final EmailAddress emailAddress, final Phone phone) {
    return new Profile(name, emailAddress, phone);
  }

  public Profile withEmailAddressOf(final EmailAddress emailAddress) {
    return new Profile(this.name, emailAddress, this.phone);
  }

  public Profile withNameOf(final PersonName name) {
    return new Profile(name, this.emailAddress, this.phone);
  }

  public Profile withPhoneOf(final Phone phone) {
    return new Profile(this.name, this.emailAddress, phone);
  }

  private Profile(final PersonName name, final EmailAddress emailAddress, final Phone phone) {
    if (name == null) throw new IllegalArgumentException("Profile name required.");
    this.name = name;

    if (emailAddress == null) throw new IllegalArgumentException("Profile email address required.");
    this.emailAddress = emailAddress;

    this.phone = phone;
  }
}
