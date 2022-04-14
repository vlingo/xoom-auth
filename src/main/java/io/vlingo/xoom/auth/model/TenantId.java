// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

import java.util.UUID;

public final class TenantId {
  public final String value;

  public static TenantId fromExisting(final String referencedId) {
    return new TenantId(referencedId);
  }

  public static TenantId unique() {
    return new TenantId();
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != TenantId.class) {
      return false;
    }
    
    final TenantId otherId = (TenantId) other;
    
    return this.value.equals(otherId.value);
  }

  @Override
  public String toString() {
    return "TenantId[value=" + value + "]";
  }

  private TenantId() {
    this.value = UUID.randomUUID().toString();
  }

  private TenantId(final String referencedId) {
    this.value = referencedId;
  }
}
