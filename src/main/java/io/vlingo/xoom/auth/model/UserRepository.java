// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.model;

import java.util.Collection;

public interface UserRepository {
  User userOf(final TenantId tenantId, final String username);
  Collection<User> usersOf(TenantId tenantId);
  void save(final User user);
}
