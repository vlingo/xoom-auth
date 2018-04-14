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

import io.vlingo.auth.model.EncodedMember.GroupMember;
import io.vlingo.auth.model.EncodedMember.RoleMember;

public final class User {
  private boolean active;
  private Set<Credential> credentials;
  private final Set<EncodedMember> memberships;
  private Profile profile;
  private final TenantId tenantId;
  private final String username;

  public static User of(final TenantId tenantId, final String username, final Profile profile, final Credential credential, final boolean active) {
    return new User(tenantId, username, profile, credential, active);
  }

  public void activate() {
    this.active = true;
  }

  public void deactivate() {
    active = false;
  }

  public boolean isActive() {
    return active;
  }

  public void add(final Credential credential) {
    if (credentials.contains(credential)) {
      throw new IllegalArgumentException("Credential already exists.");
    }
    credentials.add(credential);
  }

  public void remove(final Credential credential) {
    credentials.remove(credential);
  }

  public void replace(final Credential previousCredential, final Credential currentCredential) {
    if (!credentials.remove(previousCredential)) {
      throw new IllegalArgumentException("Missing previous credential.");
    }
    credentials.add(currentCredential);
  }

  public Set<Credential> credentials() {
    return Collections.unmodifiableSet(credentials);
  }

  public Credential vlingoCredential() {
    for (final Credential credential : credentials) {
      if (credential.isVlingo()) {
        return credential;
      }
    }
    return null;
  }

  public void replace(final Profile profile) {
    this.profile = profile;
  }

  public Profile profile() {
    return profile;
  }

  public boolean isInRole(final Role role, final Loader loader) {
    return this.isInRole(role.name(), loader);
  }

  public boolean isInRole(final String roleName, final Loader loader) {
    for (final EncodedMember member : memberships) {
      if (member.isRole() && roleName.equals(member.id)) {
        return true;
      }
    }
    for (final EncodedMember member : memberships) {
      if (member.isGroup()) {
        final Group group = loader.loadGroup(tenantId, member.id);
        if (group != null && group.isInRole(roleName, loader)) {
          return true;
        }
      }
    }
    return false;
  }

  public TenantId tenantId() {
    return tenantId;
  }

  public String username() {
    return username;
  }

  void assignTo(final Group group) {
    this.memberships.add(new GroupMember(group));
  }

  void unassignFrom(final Group group) {
    this.memberships.remove(new GroupMember(group));
  }

  void assignTo(final Role role) {
    this.memberships.add(new RoleMember(role));
  }

  void unassignFrom(final Role role) {
    this.memberships.remove(new RoleMember(role));
  }

  private User(final TenantId tenantId, final String username, final Profile profile, final Credential credential, final boolean active) {
    if (tenantId == null) throw new IllegalArgumentException("User tenant id required.");
    this.tenantId = tenantId;

    if (username == null || username.trim().isEmpty()) throw new IllegalArgumentException("User username required.");
    this.username = username;

    if (profile == null) throw new IllegalArgumentException("User profile required.");
    this.profile = profile;

    if (credential == null) throw new IllegalArgumentException("User credential required.");
    this.credentials = new HashSet<>(2);
    this.credentials.add(credential);

    this.active = active;

    this.memberships = new HashSet<>(2);
  }
}
