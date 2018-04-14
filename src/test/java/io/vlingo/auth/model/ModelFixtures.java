// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

public class ModelFixtures {

  public static Group group() {
    return group(tenant());
  }

  public static Group group(final Tenant tenant) {
    return group(tenant, "Test", "A test group.");
  }

  public static Group group(final Tenant tenant, final String name, final String description) {
    return tenant.provisionGroup(name, description);
  }

  public static Role role() {
    return role(tenant());
  }

  public static Role role(final Tenant tenant) {
    return role(tenant, "Test", "A test role.");
  }

  public static Role role(final Tenant tenant, final String name, final String description) {
    return tenant.provisionRole(name, description);
  }

  public static Tenant tenant() {
    return Tenant.with("Test", "A test tenant.", true);
  }

  public static User user() {
    return user(tenant());
  }

  public static User user(final Tenant tenant) {
    return user(tenant, "bigsecret");
  }

  public static User user(final Tenant tenant, final String secret) {
    final User user =
            tenant.registerUser(
                    "test",
                    Profile.with(PersonName.of("Given", "A", "Family"), EmailAddress.of("given@family.org"), Phone.of("303-555-1212")),
                    Credential.vlingoCredentialFrom("vlingo-platform", "given@family.org", secret),
                    true);
    return user;
  }
}
