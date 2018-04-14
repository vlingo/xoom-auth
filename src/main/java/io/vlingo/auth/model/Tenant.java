// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

public final class Tenant {
  private boolean active;
  private String description;
  private String name;
  private final TenantId tenantId;

  public static Tenant with(String name, String description, boolean active) {
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

  public Group provisionGroup(final String name, final String description) {
    return Group.with(tenantId, name, description);
  }

  public Role provisionRole(final String name, final String description) {
    return Role.with(tenantId, name, description);
  }

  public User registerUser(final String username, final Profile profile, final Credential credential, final boolean active) {
    return User.of(tenantId, username, profile, credential, active);
  }

  private Tenant(TenantId tenantId, String name, String description, boolean active) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.active = active;
  }
}
