// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.vlingo.xoom.common.crypto.Hasher;

public class Authenticator {
  private static final Duration DefaultExpiresWithin = Duration.ofMinutes(5);
  private static final UserTokenInfo NoToken = new UserTokenInfo(TenantId.fromExisting(""), "");

  private final Duration expiresWithin;
  private final Hasher hasher;
  private final UserRepository repository;
  private final Map<String,UserTokenInfo> userTokens;

  public Authenticator(final Hasher hasher, final UserRepository repository) {
    this(hasher, repository, DefaultExpiresWithin);
  }

  public Authenticator(final Hasher hasher, final UserRepository repository, final Duration expiresWithin) {
    this.hasher = hasher;
    this.repository = repository;
    this.expiresWithin = expiresWithin;
    this.userTokens = new ConcurrentHashMap<>();
  }

  public boolean authenticate(final TenantId tenantId, final String username, final String plainSecret) {
    final User user = repository.userOf(tenantId, username);

    return user != null ? hasher.verify(plainSecret, user.vlingoCredential().secret) : false;
  }

  public String authenticUserToken(final TenantId tenantId, final String username, final String plainSecret) {
    if (authenticate(tenantId, username, plainSecret)) {
      final String hashedToken = hashToken(tenantId, username, plainSecret);
      userTokens.put(hashedToken, new UserTokenInfo(tenantId, username));
      return hashedToken;
    }
    return null;
  }

  public boolean renew(final String userToken) {
    final UserTokenInfo info = userTokens.get(userToken);
    if (info != null) {
      userTokens.put(userToken, info.renew());
      return true;
    }
    return false;
  }

  public boolean isValid(final String userToken) {
    return userTokens.getOrDefault(userToken, NoToken).isValid(expiresWithin);
  }

  private String hashToken(final TenantId tenantId, final String username, final String plainSecret) {
    final String pattern = hasher.hash(userTokenFrom(tenantId, username, plainSecret));
    return pattern;
  }

  private String userTokenFrom(final TenantId tenantId, final String username, final String plainSecret) {
    return tenantId.value + ":" + username + ":" + plainSecret;
  }

  private static class UserTokenInfo {
    private final TenantId tenantId;
    private final LocalDateTime timestamp;
    private final String username;

    UserTokenInfo(final TenantId tenantId, final String username) {
      this.tenantId = tenantId;
      this.username = username;
      this.timestamp = LocalDateTime.now();
    }

    UserTokenInfo renew() {
      return new UserTokenInfo(tenantId, username);
    }

    boolean isValid(final Duration expiresWithin) {
      return timestamp.plus(expiresWithin).isAfter(LocalDateTime.now());
    }
  }
}
