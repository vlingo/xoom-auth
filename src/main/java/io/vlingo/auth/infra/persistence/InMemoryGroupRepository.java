// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.persistence;

import java.util.HashMap;
import java.util.Map;

import io.vlingo.auth.model.Group;
import io.vlingo.auth.model.GroupRepository;
import io.vlingo.auth.model.TenantId;

public class InMemoryGroupRepository extends BaseRepository implements GroupRepository {
  private final Map<String, Group> groups = new HashMap<>();

  @Override
  public Group groupOf(final TenantId tenantId, final String groupName) {
    final Group group = groups.get(keyFor(tenantId, groupName));
    return group == null ? Group.NonExisting : group;
  }

  @Override
  public void save(final Group group) {
    groups.put(keyFor(group.tenantId(), group.name()), group);
  }
}
