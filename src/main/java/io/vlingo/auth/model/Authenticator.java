// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import io.vlingo.auth.model.crypto.Hasher;

public class Authenticator {
  private final Hasher hasher;
  private final UserRepository repository;
  
  public Authenticator(final Hasher hasher, final UserRepository repository) {
    this.hasher = hasher;
    this.repository = repository;
  }

  public boolean authenticate(final TenantId tenantId, final String username, final String plainSecret) {
    final User user = repository.userOf(tenantId, username);
    
    return user != null ? hasher.verify(plainSecret, user.vlingoCredential().secret) : false;
  }
}
