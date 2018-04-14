// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

public final class Role {
  private static final String RoleGroupPrefix = "role:";

  private String description;
  private final Group assigned;
  private final String name;
  private final TenantId tenantId;

  public static Role with(final TenantId tenantId, final String name, final String description) {
    return new Role(tenantId, name, description);
  }

  public void changeDescription(final String description) {
    this.description = description;
  }

  public String description() {
    return description;
  }

  public void assign(final Group group) {
    assigned.assign(group);
    
    group.assignTo(this);
  }

  public void unassign(final Group group) {
    assigned.unassign(group);
    
    group.unassignFrom(this);
  }

  public void assign(final User user) {
    assigned.assign(user);
    
    user.assignTo(this);
  }

  public void unassign(final User user) {
    assigned.unassignFrom(user);
    
    user.unassignFrom(this);
  }

  public boolean isInRole(final Group group, final Loader loader) {
    if (assigned.isMember(group, loader)) {
      return true;
    }
    return false; //group.isInRole(this, loader);
  }

  public boolean isInRole(final User user, final Loader loader) {
    if (assigned.isMember(user, loader)) {
      return true;
    }
    return false; //user.isInRole(this, loader);
  }

  public String name() {
    return name;
  }

  public TenantId tenantId() {
    return tenantId;
  }

  @Override
  public String toString() {
    return "Role[tenantId=" + tenantId + " name=" + name + " description=" + description + "]";
  }

  private Role(final TenantId tenantId, final String name, final String description) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.assigned = Group.forRole(tenantId, RoleGroupPrefix + name, "Internal group for role " + name + ".");
  }
}
