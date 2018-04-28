// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.vlingo.auth.model.EncodedMember.PermissionMember;

public final class Role {
  public static final Role NonExisting = new Role(null, null, null);

  private static final String RoleGroupPrefix = "role:";

  private String description;
  private final Group assigned;
  private final String name;
  private final Set<EncodedMember> permissions;
  private final TenantId tenantId;

  public static Role with(final TenantId tenantId, final String name, final String description) {
    return new Role(tenantId, name, description);
  }

  public boolean doesNotExist() {
    return this.tenantId == null || this.name == null;
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
    assigned.unassign(user);

    user.unassignFrom(this);
  }

  public void attach(final Permission permission) {
    permissions.add(new PermissionMember(permission));
  }

  public void detach(final Permission permission) {
    permissions.remove(new PermissionMember(permission));
  }

  public boolean hasPermissionOf(final String name) {
    for (final EncodedMember member : permissions) {
      if (member.id.equals(name)) {
        return true;
      }
    }
    return false;
  }

  public Permission permissionOf(final String name, final Loader loader) {
    for (final EncodedMember member : permissions) {
      if (member.id.equals(name)) {
        return loader.loadPermission(tenantId, member.id);
      }
    }
    return null;
  }

  public Set<Permission> permissions(final Loader loader) {
    final Set<Permission> loaded = new HashSet<>(permissions.size());

    for (final EncodedMember member : permissions) {
      loaded.add(loader.loadPermission(tenantId, member.id));
    }

    return Collections.unmodifiableSet(loaded);
  }

  public boolean isInRole(final Group group, final Loader loader) {
    if (assigned.hasMember(group, loader)) {
      return true;
    }
    return false;
  }

  public boolean isInRole(final User user, final Loader loader) {
    if (assigned.hasMember(user, loader)) {
      return true;
    }
    return false;
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
    this.permissions = new HashSet<>(2);
  }
}
