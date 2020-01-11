// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Permission {
  public static final Permission NonExisting = new Permission(null, null, null);

  private final Set<Constraint> constraints;
  private String description;
  private final String name;
  private final TenantId tenantId;

  public static Permission with(final TenantId tenantId, final String name, final String description) {
    return new Permission(tenantId, name, description);
  }

  public boolean doesNotExist() {
    return this.tenantId == null || this.name == null;
  }

  public void enforce(final Constraint constraint) {
    constraints.add(constraint);
  }

  public void enforce(final Constraint previousConstraint, final Constraint currentConstraint) {
    if (!constraints.remove(previousConstraint)) {
      throw new IllegalArgumentException("Missing previous constraint.");
    }
    constraints.add(currentConstraint);
  }

  public void forget(final Constraint constraint) {
    constraints.remove(constraint);
  }

  public Constraint constraintOf(final String name) {
    for (final Constraint constraint : constraints) {
      if (constraint.name.equals(name)) {
        return constraint;
      }
    }
    return null;
  }

  public Set<Constraint> constraints() {
    return Collections.unmodifiableSet(constraints);
  }

  public void changeDescription(final String description) {
    this.description = description;
  }

  public String description() {
    return description;
  }

  public String name() {
    return name;
  }

  public TenantId tenantId() {
    return tenantId;
  }

  @Override
  public int hashCode() {
    return 31 * (tenantId.hashCode() + name.hashCode());
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != Permission.class) {
      return false;
    }

    final Permission otherPermission = (Permission) other;

    return this.tenantId.equals(otherPermission.tenantId) && this.name.equals(otherPermission.name);
  }

  @Override
  public String toString() {
    return "Permission[tenantId=" + tenantId + " name=" + name + " description=" + description + " constraints=" + constraints + "]";
  }

  private Permission(final TenantId tenantId, final String name, final String description) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;

    this.constraints = new HashSet<>(2);
  }
}
