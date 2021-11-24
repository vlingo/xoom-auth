package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;

import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.common.Completes;

@SuppressWarnings("all")
public interface RoleQueries {
  Completes<RoleView> roleOf(RoleId roleId);
  Completes<Collection<RoleView>> roles();
}