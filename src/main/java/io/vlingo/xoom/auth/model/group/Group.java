package io.vlingo.xoom.auth.model.group;

import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.common.Completes;

public interface Group {

  Completes<GroupState> provisionGroup(final String name, final String description);

  Completes<GroupState> changeDescription(final String description);

  Completes<GroupState> assignGroup(final GroupId groupId);

  Completes<GroupState> unassignGroup(final GroupId groupId);

  Completes<GroupState> assignUser(final UserId userId);

  Completes<GroupState> unassignUser(final UserId userId);
}