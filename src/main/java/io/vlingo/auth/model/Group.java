// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import java.util.HashSet;
import java.util.Set;

import io.vlingo.auth.model.EncodedMember.GroupMember;
import io.vlingo.auth.model.EncodedMember.RoleMember;
import io.vlingo.auth.model.EncodedMember.UserMember;

public final class Group {
  public static final Group NonExisting = new Group(null, null, null, null);

  private enum Type { Group, RoleGroup }

  private String description;
  private final Set<EncodedMember> members;
  private final String name;
  private final Set<EncodedMember> outer;
  private final Set<EncodedMember> roles;
  private final TenantId tenantId;
  private final Type type;

  public static Group with(final TenantId tenantId, final String name, final String description) {
    return new Group(tenantId, name, description, Type.Group);
  }

  static Group forRole(final TenantId tenantId, final String name, final String description) {
    return new Group(tenantId, name, description, Type.RoleGroup);
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
    if (members.add(new GroupMember(group))) {
      if (this.isNormalGroup()) {
        group.assignTo(this);
      }
    }
  }

  public void unassign(final Group group) {
    if (members.remove(new GroupMember(group))) {
      if (this.isNormalGroup()) {
        group.unassignFrom(this);
      }
    }
  }

  public void assign(final User user) {
    if (members.add(new UserMember(user))) {
      if (this.isNormalGroup()) {
        user.assignTo(this);
      }
    }
  }

  public void unassign(final User user) {
    if (members.remove(new UserMember(user))) {
      if (this.isNormalGroup()) {
        user.unassignFrom(this);
      }
    }
  }

  public boolean isMember(final Group group, final Loader loader) {
    return isMember(new GroupMember(group), loader);
  }

  public boolean isMember(final User user, final Loader loader) {
    return isMember(new UserMember(user), loader);
  }

  public String name() {
    return name;
  }

  public boolean hasPermission(final Permission permission, final Loader loader) {
    return hasPermission(permission.name(), loader);
  }

  public boolean hasPermission(String permissionName, final Loader loader) {
    for (final EncodedMember member : roles) {
      final Role role = loader.loadRole(tenantId, member.id);
      if (role != null && role.hasPermissionOf(permissionName)) {
        return true;
      }
    }
    for (final EncodedMember member : outer) {
      if (member.isGroup()) {
        final Group group = loader.loadGroup(tenantId, member.id);
        if (group != null && group.isNormalGroup() && group.hasPermission(permissionName, loader)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isInRole(final Role role, final Loader loader) {
    return this.isInRole(role.name(), loader);
  }

  public boolean isInRole(final String roleName, final Loader loader) {
    for (final EncodedMember member : roles) {
      if (member.isRole() && roleName.equals(member.id)) {
        return true;
      }
    }
    for (final EncodedMember member : outer) {
      if (member.isGroup()) {
        final Group group = loader.loadGroup(tenantId, member.id);
        if (group != null && group.isNormalGroup() && group.isInRole(roleName, loader)) {
          return true;
        }
      }
    }
    return false;
  }

  public TenantId tenantId() {
    return tenantId;
  }

  @Override
  public String toString() {
    return "Group[tenantId=" + tenantId + " name=" + name + " description=" + description + " type=" + type + " members=" + members + "]";
  }

  void assignTo(final Group group) {
    this.outer.add(new GroupMember(group));
  }

  void unassignFrom(final Group group) {
    this.roles.remove(new GroupMember(group));
  }

  void assignTo(final Role role) {
    this.roles.add(new RoleMember(role));
  }

  void unassignFrom(final Role role) {
    this.roles.remove(new RoleMember(role));
  }

  boolean isNormalGroup() {
    return type == Type.Group;
  }

  boolean isRoleGroup() {
    return type == Type.RoleGroup;
  }

  private Group(final TenantId tenantId, final String name, final String description, final Type type) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.type = type;

    this.members = new HashSet<>(10);
    this.outer = new HashSet<>(2);
    this.roles = new HashSet<>(2);
  }

  private boolean isMember(final EncodedMember possibleMember, final Loader loader) {
    if (members.contains(possibleMember)) {
      return true;
    }

    for (final EncodedMember actualMember : members) {
      if (actualMember.isGroup()) {
        final Group inner = loader.loadGroup(tenantId, actualMember.id);
        if (inner != null && inner.isMember(possibleMember, loader)) {
          return true;
        }
      }
    }
    return false;
  }
}
