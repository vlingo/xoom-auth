// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infra.resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.vlingo.xoom.auth.model.Role;

public final class RoleData {
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

  public static Collection<RoleData> from(final Collection<Role> roles) {
    final Set<RoleData> roleData = new HashSet<>();
    for (final Role role : roles) {
      roleData.add(from(role));
    }
    return roleData;
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
