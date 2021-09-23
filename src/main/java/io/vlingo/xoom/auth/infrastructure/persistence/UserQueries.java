package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;

import io.vlingo.xoom.auth.infrastructure.UserRegistrationData;
import io.vlingo.xoom.common.Completes;

@SuppressWarnings("all")
public interface UserQueries {
  Completes<UserRegistrationData> userOf(String id);
  Completes<Collection<UserRegistrationData>> users();
}