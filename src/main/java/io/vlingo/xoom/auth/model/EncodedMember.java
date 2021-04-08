// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

abstract class EncodedMember {
  static final char GroupType = 'G';
  static final char PermissionType = 'P';
  static final char RoleType = 'R';
  static final char UserType = 'U';

  final String id;
  final char type;

  EncodedMember(final String id, final char type) {
    this.id = id;
    this.type = type;
  }

  boolean isGroup() {
    return type == GroupType;
  }

  boolean isPermission() {
    return type == PermissionType;
  }

  boolean isRole() {
    return type == RoleType;
  }

  boolean isUser() {
    return type == UserType;
  }

  boolean isSameAs(final char type) {
    return this.type == type;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != this.getClass()) {
      return false;
    }

    final EncodedMember otherMember = (EncodedMember) other;

    return this.type == otherMember.type && this.id.equals(otherMember.id);
  }

  @Override
  public int hashCode() {
    return 31 * (type + id.hashCode());
  }

  @Override
  public String toString() {
    return "EncodedMember[type=" + type + " id=" + id + "]";
  }

  static final class GroupMember extends EncodedMember {
    GroupMember(final Group group) {
      super(group.name(), GroupType);
    }
  }

  static final class PermissionMember extends EncodedMember {
    PermissionMember(final Permission permission) {
      super(permission.name(), PermissionType);
    }
  }

  static final class RoleMember extends EncodedMember {
    RoleMember(final Role role) {
      super(role.name(), RoleType);
    }
  }

  static final class UserMember extends EncodedMember {
    UserMember(final User user) {
      super(user.username(), UserType);
    }
  }
}
