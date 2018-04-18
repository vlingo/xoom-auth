// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

public final class ProfileData {
  public final String emailAddress;
  public final PersonNameData name;
  public final String phone;

  public static ProfileData from(final PersonNameData name, final String emailAddress, final String phone) {
    return new ProfileData(name, emailAddress, phone);
  }

  public ProfileData(final PersonNameData name, final String emailAddress, final String phone) {
    if (name == null) throw new IllegalArgumentException("Name required.");
    this.name = name;

    if (emailAddress == null) throw new IllegalArgumentException("Email address required.");
    this.emailAddress = emailAddress;

    this.phone = phone;
  }
}
