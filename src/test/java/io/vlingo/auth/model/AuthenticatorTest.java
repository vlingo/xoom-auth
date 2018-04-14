// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import static io.vlingo.auth.model.ModelFixtures.tenant;
import static io.vlingo.auth.model.ModelFixtures.user;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.vlingo.auth.model.crypto.Argon2Hasher;
import io.vlingo.auth.model.crypto.BCryptHasher;
import io.vlingo.auth.model.crypto.Hasher;
import io.vlingo.auth.model.crypto.SCryptHasher;

public class AuthenticatorTest {

  @Test
  public void testThatUserAuthenticatesWithArgon2() {
    final Hasher hasher = new Argon2Hasher(2, 65535, 1);
    final Tenant tenant = tenant();
    final String secret = "asecret-argon2-secret";
    final User user = user(tenant, hasher.hash(secret));
    final UserRepository repository = new TestUserRepository();
    repository.save(user);
    
    final Authenticator authenticator = new Authenticator(hasher, repository);
    
    assertTrue(authenticator.authenticate(tenant.tenantId(), user.username(), secret));
  }

  @Test
  public void testThatUserAuthenticatesWithBcrypt() {
    final Hasher hasher = new BCryptHasher();
    final Tenant tenant = tenant();
    final String secret = "asecret-bcrypt-secret";
    final User user = user(tenant, hasher.hash(secret));
    final UserRepository repository = new TestUserRepository();
    repository.save(user);
    
    final Authenticator authenticator = new Authenticator(hasher, repository);
    
    assertTrue(authenticator.authenticate(tenant.tenantId(), user.username(), secret));
  }

  @Test
  public void testThatUserAuthenticatesWithScrypt() {
    final Hasher hasher = new SCryptHasher(16384, 8, 1);
    final Tenant tenant = tenant();
    final String secret = "asecret-scrypt-secret";
    final User user = user(tenant, hasher.hash(secret));
    final UserRepository repository = new TestUserRepository();
    repository.save(user);
    
    final Authenticator authenticator = new Authenticator(hasher, repository);
    
    assertTrue(authenticator.authenticate(tenant.tenantId(), user.username(), secret));
  }
}
