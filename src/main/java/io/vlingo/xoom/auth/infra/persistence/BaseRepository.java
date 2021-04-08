// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infra.persistence;

import io.vlingo.xoom.auth.model.TenantId;

abstract class BaseRepository {
  protected String keyFor(final TenantId tenantId) {
    return tenantId.value + ":";
  }

  protected String keyFor(final TenantId tenantId, final String groupName) {
    return tenantId.value + ":" + groupName;
  }
}
