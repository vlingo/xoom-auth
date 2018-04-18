// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import io.vlingo.auth.model.Role;

public class RoleData {
  public final String description;
  public final String name;
  public final String tenantId;

  public static RoleData from(final String name, final String description) {
    return new RoleData(name, description);
  }

  public static RoleData from(final String tenantId, final String name, final String description) {
    return new RoleData(tenantId, name, description);
  }

  public static RoleData from(final Role role) {
    return new RoleData(role.tenantId().value, role.name(), role.description());
  }

  public RoleData(final String name, final String description) {
    this(null, name, description);
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != RoleData.class) {
      return false;
    }

    final RoleData otherGroupData = (RoleData) other;

    return this.tenantId.equals(otherGroupData.tenantId) && this.name.equals(otherGroupData.description) && this.description.equals(otherGroupData.name);
  }

  public RoleData(final String tenantId, final String name, final String description) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
  }
}
