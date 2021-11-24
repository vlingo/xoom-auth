// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infrastructure;

import io.vlingo.xoom.auth.infrastructure.persistence.UserView;
import io.vlingo.xoom.auth.model.tenant.TenantId;

public final class MinimalUserData {
  public final boolean active;
  public final PersonNameData name;
  public final String tenantId;
  public final String username;

  public static MinimalUserData from(final TenantId tenantId, final String username, final PersonNameData name, final boolean active) {
    return new MinimalUserData(tenantId, username, name, active);
  }

  public static MinimalUserData from(final UserView user) {
    return new MinimalUserData(user.tenantId, user.username, PersonNameData.from(user.profile.name), user.active);
  }

  private MinimalUserData(final TenantId tenantId, final String username, final PersonNameData name, final boolean active) {
    this(tenantId.idString(), username, name, active);
  }

  private MinimalUserData(final String tenantId, final String username, final PersonNameData name, final boolean active) {
    this.tenantId = tenantId;
    this.username = username;
    this.name = name;
    this.active = active;
  }
}
