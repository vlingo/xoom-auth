package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.actors.Definition;
import java.util.*;
import io.vlingo.xoom.auth.model.value.*;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.actors.Stage;

public interface Permission {

  Completes<PermissionState> provisionPermission(final String name, final String description, final String tenantId);

  static Completes<PermissionState> provisionPermission(final Stage stage, final String description, final String name, final String tenantId) {
    final io.vlingo.xoom.actors.Address _address = stage.addressFactory().uniquePrefixedWith("g-");
    final Permission _permission = stage.actorFor(Permission.class, Definition.has(PermissionEntity.class, Definition.parameters(_address.idString())), _address);
    return _permission.provisionPermission(name, description, tenantId);
  }

  Completes<PermissionState> enforce(final Constraint constraint);

  Completes<PermissionState> enforceReplacement(final Constraint constraint);

  Completes<PermissionState> forget(final Constraint constraint);

  Completes<PermissionState> changeDescription(final String description, final String tenantId);

}