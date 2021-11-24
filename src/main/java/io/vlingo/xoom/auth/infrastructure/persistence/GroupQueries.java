package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.common.Completes;

@SuppressWarnings("all")
public interface GroupQueries {
  Completes<GroupView> groupOf(GroupId groupId);
  Completes<Collection<GroupView>> groups();
}