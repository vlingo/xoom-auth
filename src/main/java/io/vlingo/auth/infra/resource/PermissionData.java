// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.vlingo.auth.model.Constraint;
import io.vlingo.auth.model.Permission;

public final class PermissionData {
  public final Set<ConstraintData> constraints;
  public String description;
  public final String name;
  public final String tenantId;

  public static PermissionData from(final String tenantId, final String name, final String description) {
    return new PermissionData(tenantId, name, description);
  }

  public static PermissionData from(final String name, final String description) {
    return new PermissionData(null, name, description);
  }

  public static PermissionData from(final Permission permission) {
    final PermissionData permissionData = new PermissionData(permission.tenantId().value, permission.name(), permission.description());
    for (final Constraint constraint : permission.constraints()) {
      permissionData.constraints.add(ConstraintData.from(constraint));
    }
    return permissionData;
  }

  public static Collection<PermissionData> from(final Collection<Permission> permissions) {
    final Set<PermissionData> permissionData = new HashSet<>();
    for (final Permission permission : permissions) {
      permissionData.add(from(permission));
    }
    return permissionData;
  }

  private PermissionData(final String tenantId, final String name, final String description) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;

    this.constraints = new HashSet<>(2);
  }
}
