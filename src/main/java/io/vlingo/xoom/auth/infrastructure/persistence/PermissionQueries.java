package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;
import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.auth.infrastructure.PermissionData;

@SuppressWarnings("all")
public interface PermissionQueries {
  Completes<PermissionData> permissionOf(String id);
  Completes<Collection<PermissionData>> permissions();
}