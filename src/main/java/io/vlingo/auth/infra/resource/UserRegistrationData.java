// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import io.vlingo.auth.model.Credential;
import io.vlingo.auth.model.User;

public final class UserRegistrationData {
  public final boolean active;
  public final CredentialData credential;
  public final ProfileData profile;
  public final String tenantId;
  public final String username;

  public static UserRegistrationData from(final String tenantId, final String username, final ProfileData profile, final CredentialData credential, final boolean active) {
    return new UserRegistrationData(tenantId, username, profile, credential, active);
  }

  public static UserRegistrationData from(final User user) {
    final Credential credential = user.vlingoCredential();
    return new UserRegistrationData(
            user.tenantId().value,
            user.username(),
            ProfileData.from(
                    PersonNameData.of(user.profile().name.given, user.profile().name.second, user.profile().name.family),
                    user.profile().emailAddress.value,
                    user.profile().phone.value),
            CredentialData.from(credential.authority, credential.id, "********", credential.type.name()),
            user.isActive());
  }

  private UserRegistrationData(final String tenantId, final String username, final ProfileData profile, final CredentialData credential, final boolean active) {
    this.tenantId = tenantId;
    this.username = username;
    this.profile = profile;
    this.credential = credential;
    this.active = active;
  }
}