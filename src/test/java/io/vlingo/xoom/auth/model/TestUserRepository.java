// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestUserRepository implements UserRepository {
  private final Map<String,User> users = new HashMap<>();

  @Override
  public User userOf(final TenantId tenantId, final String username) {
    return users.get(keyFor(tenantId, username));
  }

  @Override
  public Collection<User> usersOf(TenantId tenantId) {
    final Set<User> tenantUsers = new HashSet<>();
    final String tenantKey = keyFor(tenantId, "");
    for (final String key : users.keySet()) {
      if (key.startsWith(tenantKey)) {
        tenantUsers.add(users.get(key));
      }
    }
    return tenantUsers;
  }

  @Override
  public void save(final User user) {
    users.put(keyFor(user.tenantId(), user.username()), user);
  }

  private String keyFor(final TenantId tenantId, final String username) {
    return tenantId.value + ":" + username;
  }
}
