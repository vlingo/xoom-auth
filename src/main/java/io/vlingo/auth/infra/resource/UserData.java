// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import java.util.List;

import io.vlingo.auth.model.User;

public final class UserData {
  public final boolean active;
  public final List<CredentialData> credentials;
  public final ProfileData profile;
  public final String tenantId;
  public final String username;

  public static UserData from(final String tenantId, final String username, final ProfileData profile, final List<CredentialData> credentials, final boolean active) {
    return new UserData(tenantId, username, profile, credentials, active);
  }

  public static UserData from(final User user) {
    return new UserData(
            user.tenantId().value,
            user.username(),
            ProfileData.from(user),
            CredentialData.from(user),
            user.isActive());
  }

  public CredentialData credentialOf(final String authority) {
    for (final CredentialData credential : credentials) {
      if (credential.authority.equals(authority)) {
        return credential;
      }
    }
    return null;
  }

  private UserData(final String tenantId, final String username, final ProfileData profile, final List<CredentialData> credentials, final boolean active) {
    this.tenantId = tenantId;
    this.username = username;
    this.profile = profile;
    this.credentials = credentials;
    this.active = active;
  }
}
