package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.auth.model.value.Constraint;
import io.vlingo.xoom.common.Completes;

public interface Permission {

  Completes<PermissionState> provisionPermission(final String name, final String description);

  Completes<PermissionState> enforce(final Constraint constraint);

  Completes<PermissionState> enforceReplacement(String constraintName, final Constraint constraint);

  Completes<PermissionState> forget(final Constraint constraint);

  Completes<PermissionState> changeDescription(final String description);

}