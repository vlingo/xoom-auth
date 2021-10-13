package io.vlingo.xoom.auth.model.group;

import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.common.Completes;

public interface Group {

  Completes<GroupState> provisionGroup(final String name, final String description);

  Completes<GroupState> changeDescription(final String description);

  Completes<GroupState> assignGroup(final GroupId groupId);

  Completes<GroupState> unassignGroup(final GroupId groupId);

  Completes<GroupState> assignUser(final TenantId tenantId);

  Completes<GroupState> unassignUser(final TenantId tenantId);

}