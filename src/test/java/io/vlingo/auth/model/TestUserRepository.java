// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import java.util.HashMap;
import java.util.Map;

public class TestUserRepository implements UserRepository {
  private final Map<String,User> users = new HashMap<>();

  @Override
  public User userOf(final TenantId tenantId, final String username) {
    return users.get(keyFor(tenantId, username));
  }

  @Override
  public void save(final User user) {
    users.put(keyFor(user.tenantId(), user.username()), user);
  }

  private String keyFor(final TenantId tenantId, final String username) {
    return tenantId.value + ":" + username;
  }
}
