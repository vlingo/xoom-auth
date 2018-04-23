// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.persistence;

import java.util.HashMap;
import java.util.Map;

import io.vlingo.auth.model.TenantId;
import io.vlingo.auth.model.User;
import io.vlingo.auth.model.UserRepository;

public class InMemoryUserRepository extends BaseRepository implements UserRepository {
  private final Map<String, User> users = new HashMap<>();

  @Override
  public User userOf(final TenantId tenantId, final String username) {
    final User user = users.get(keyFor(tenantId, username));
    return user == null ? User.NonExisting : user;
  }

  @Override
  public void save(final User user) {
    users.put(keyFor(user.tenantId(), user.username()), user);
  }
}
