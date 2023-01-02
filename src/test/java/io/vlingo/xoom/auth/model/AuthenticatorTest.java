// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

import static io.vlingo.xoom.auth.model.ModelFixtures.tenant;
import static io.vlingo.xoom.auth.model.ModelFixtures.user;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;

import org.junit.Test;

import io.vlingo.xoom.common.crypto.Argon2Hasher;
import io.vlingo.xoom.common.crypto.BCryptHasher;
import io.vlingo.xoom.common.crypto.Hasher;
import io.vlingo.xoom.common.crypto.SCryptHasher;

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

  @Test
  public void testThatUserTokenValidates() {
    final Hasher hasher = new Argon2Hasher(2, 65535, 1);
    final Tenant tenant = tenant();
    final String secret = "asecret-argon2-secret";
    final User user = user(tenant, hasher.hash(secret));
    final UserRepository repository = new TestUserRepository();
    repository.save(user);

    final Authenticator authenticator = new Authenticator(hasher, repository, Duration.ofMillis(10));

    final String userToken = authenticator.authenticUserToken(tenant.tenantId(), user.username(), secret);
    assertNotNull(userToken);

    assertTrue(authenticator.isValid(userToken));
  }

  @Test
  public void testThatUserTokenExpires() {
    final Hasher hasher = new Argon2Hasher(2, 65535, 1);
    final Tenant tenant = tenant();
    final String secret = "asecret-argon2-secret";
    final User user = user(tenant, hasher.hash(secret));
    final UserRepository repository = new TestUserRepository();
    repository.save(user);

    final Authenticator authenticator = new Authenticator(hasher, repository, Duration.ofMillis(1));

    final String userToken = authenticator.authenticUserToken(tenant.tenantId(), user.username(), secret);
    assertNotNull(userToken);

    try { Thread.sleep(5); } catch (Exception e) { }

    assertFalse(authenticator.isValid(userToken));
  }

  @Test
  public void testThatUserTokenRenews() {
    final Hasher hasher = new Argon2Hasher(2, 65535, 1);
    final Tenant tenant = tenant();
    final String secret = "asecret-argon2-secret";
    final User user = user(tenant, hasher.hash(secret));
    final UserRepository repository = new TestUserRepository();
    repository.save(user);

    final Authenticator authenticator = new Authenticator(hasher, repository, Duration.ofMillis(10));

    final String userToken = authenticator.authenticUserToken(tenant.tenantId(), user.username(), secret);
    assertNotNull(userToken);

    try { Thread.sleep(5); } catch (Exception e) { }

    authenticator.renew(userToken);

    try { Thread.sleep(5); } catch (Exception e) { }

    assertTrue(authenticator.isValid(userToken));
  }

  @Test
  public void testThatUserTokenRemainsValid() {
    final Hasher hasher = new Argon2Hasher(2, 65535, 1);
    final Tenant tenant = tenant();
    final String secret = "asecret-argon2-secret";
    final User user = user(tenant, hasher.hash(secret));
    final UserRepository repository = new TestUserRepository();
    repository.save(user);

    final Authenticator authenticator = new Authenticator(hasher, repository, Duration.ofMillis(20));

    final String userToken = authenticator.authenticUserToken(tenant.tenantId(), user.username(), secret);
    assertNotNull(userToken);

    try { Thread.sleep(7); } catch (Exception e) { }

    assertTrue(authenticator.isValid(userToken));
  }
}
