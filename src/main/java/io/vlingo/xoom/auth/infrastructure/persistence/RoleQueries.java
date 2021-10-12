package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;

import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.auth.infrastructure.RoleData;

@SuppressWarnings("all")
public interface RoleQueries {
  Completes<RoleData> roleOf(RoleId roleId);
  Completes<Collection<RoleData>> roles();
}