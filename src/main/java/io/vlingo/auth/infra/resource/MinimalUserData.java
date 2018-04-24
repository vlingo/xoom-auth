// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.vlingo.auth.model.User;

public final class MinimalUserData {
  public final boolean active;
  public final PersonNameData name;
  public final String tenantId;
  public final String username;

  public static MinimalUserData from(final String tenantId, final String username, final PersonNameData name, final boolean active) {
    return new MinimalUserData(tenantId, username, name, active);
  }

  public static MinimalUserData from(final User user) {
    return new MinimalUserData(
            user.tenantId().value,
            user.username(),
            PersonNameData.of(user.profile().name.given, user.profile().name.second, user.profile().name.family),
            user.isActive());
  }

  public static Collection<MinimalUserData> from(final Collection<User> users) {
    final Set<MinimalUserData> userData = new HashSet<>();
    for (final User user : users) {
      userData.add(from(user));
    }
    return userData;
  }

  private MinimalUserData(final String tenantId, final String username, final PersonNameData name, final boolean active) {
    this.tenantId = tenantId;
    this.username = username;
    this.name = name;
    this.active = active;
  }
}
