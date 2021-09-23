package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;
import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.auth.infrastructure.UserData;

@SuppressWarnings("all")
public interface UserQueries {
  Completes<UserData> userOf(String id);
  Completes<Collection<UserData>> users();
}