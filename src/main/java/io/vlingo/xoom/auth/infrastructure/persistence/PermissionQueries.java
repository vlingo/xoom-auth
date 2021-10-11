package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;

import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.auth.infrastructure.PermissionData;

@SuppressWarnings("all")
public interface PermissionQueries {
  Completes<PermissionData> permissionOf(PermissionId permissionId);
  Completes<Collection<PermissionData>> permissions();
}