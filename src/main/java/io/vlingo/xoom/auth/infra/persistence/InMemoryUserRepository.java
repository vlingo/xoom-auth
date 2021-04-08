// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infra.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.vlingo.xoom.auth.model.TenantId;
import io.vlingo.xoom.auth.model.User;
import io.vlingo.xoom.auth.model.UserRepository;

public class InMemoryUserRepository extends BaseRepository implements UserRepository {
  private final Map<String, User> users = new HashMap<>();

  @Override
  public User userOf(final TenantId tenantId, final String username) {
    final User user = users.get(keyFor(tenantId, username));
    return user == null ? User.NonExisting : user;
  }

  @Override
  public Collection<User> usersOf(TenantId tenantId) {
    final Set<User> tenantUsers = new HashSet<>();
    final String tenantKey = keyFor(tenantId);
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
}
