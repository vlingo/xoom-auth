package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;
import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.auth.infrastructure.GroupData;

@SuppressWarnings("all")
public interface GroupQueries {
  Completes<GroupData> groupOf(String id);
  Completes<Collection<GroupData>> groups();
}