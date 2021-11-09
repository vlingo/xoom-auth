package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;

import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.common.Completes;

@SuppressWarnings("all")
public interface PermissionQueries {
  Completes<PermissionView> permissionOf(PermissionId permissionId);
  Completes<Collection<PermissionView>> permissions();
}