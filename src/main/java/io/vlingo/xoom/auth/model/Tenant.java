// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

public final class Tenant {
  public static final Tenant NonExisting = new Tenant(null, null, null, false);

  private boolean active;
  private String description;
  private String name;
  private final TenantId tenantId;

  public static Tenant subscribeFor(String name, String description, boolean active) {
    return new Tenant(TenantId.unique(), name, description, active);
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

  public void changeDescription(final String description) {
    this.description = description;
  }

  public String description() {
    return description;
  }

  public void changeName(final String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public TenantId tenantId() {
    return tenantId;
  }

  public boolean doesNotExist() {
    return tenantId == null;
  }

  public Group provisionGroup(final String name, final String description) {
    return Group.with(tenantId, name, description);
  }

  public Permission provisionPermission(final String name, final String description) {
    return Permission.with(tenantId, name, description);
  }

  public Role provisionRole(final String name, final String description) {
    return Role.with(tenantId, name, description);
  }

  public User registerUser(final String username, final Profile profile, final Credential credential, final boolean active) {
    return User.of(tenantId, username, profile, credential, active);
  }

  @Override
  public int hashCode() {
    return 31 * this.tenantId.value.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != Tenant.class) {
      return false;
    }
    
    return this.tenantId.equals(((Tenant) other).tenantId);
  }

  @Override
  public String toString() {
    return "Tenant[tenantId=" + tenantId + " name=" + name + " description=" + description + " active=" + active + "]";
  }

  private Tenant(TenantId tenantId, String name, String description, boolean active) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.active = active;
  }
}
