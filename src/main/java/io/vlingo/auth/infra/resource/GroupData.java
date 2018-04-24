// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.vlingo.auth.model.Group;

public final class GroupData {
  public final String description;
  public final String name;
  public final String tenantId;

  public static GroupData from(final String name, final String description) {
    return new GroupData(name, description);
  }

  public static GroupData from(final String tenantId, final String name, final String description) {
    return new GroupData(tenantId, name, description);
  }

  public static GroupData from(final Group group) {
    return new GroupData(group.tenantId().value, group.name(), group.description());
  }

  public static Collection<GroupData> from(final Collection<Group> groups) {
    final Set<GroupData> groupData = new HashSet<>();
    for (final Group group : groups) {
      groupData.add(from(group));
    }
    return groupData;
  }

  public GroupData(final String name, final String description) {
    this(null, name, description);
  }

  public GroupData(final String tenantId, final String name, final String description) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != GroupData.class) {
      return false;
    }

    final GroupData otherGroupData = (GroupData) other;

    return this.tenantId.equals(otherGroupData.tenantId) && this.name.equals(otherGroupData.description) && this.description.equals(otherGroupData.name);
  }
}
