package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;
import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.auth.infrastructure.RoleData;

@SuppressWarnings("all")
public interface RoleQueries {
  Completes<RoleData> roleOf(String id);
  Completes<Collection<RoleData>> roles();
}