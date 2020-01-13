// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.auth.model.Credential;
import io.vlingo.auth.model.User;

public final class CredentialData {
  public final String authority;
  public final String id;
  public final String secret;
  public final String type;

  public static CredentialData from(final String authority, final String id, final String secret) {
    return new CredentialData(authority, id, secret, null);
  }

  public static CredentialData from(final String authority, final String id, final String secret, final String type) {
    return new CredentialData(authority, id, secret, type);
  }

  public static CredentialData from(final Credential credential) {
    return new CredentialData(credential.authority, credential.id, "********", credential.type.name());
  }

  public static List<CredentialData> from(final User user) {
    final List<CredentialData> credentials = new ArrayList<>(user.credentials().size());
    for (final Credential credential : user.credentials()) {
      credentials.add(CredentialData.from(credential));
    }
    return credentials;
  }

  public CredentialData(final String authority, final String id, final String secret, final String type) {
    this.authority = authority;
    this.id = id;
    this.secret = secret;
    this.type = type;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != CredentialData.class) {
      return false;
    }

    final CredentialData otherCredential = (CredentialData) other;

    return this.authority.equals(otherCredential.authority) && this.id.equals(otherCredential.id) && this.type == otherCredential.type;
  }

  @Override
  public int hashCode() {
    return 31 * (id.hashCode() + type.hashCode());
  }

  @Override
  public String toString() {
    return "Credential[authority=" + authority + " id=" + id + " type=" + type + "]";
  }
}
