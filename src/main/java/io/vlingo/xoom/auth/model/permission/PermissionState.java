package io.vlingo.xoom.auth.model.permission;

import java.util.*;
import io.vlingo.xoom.auth.model.value.*;

public final class PermissionState {

  public final PermissionId id;
  public final Set<Constraint> constraints = new HashSet<>();
  public final String name;
  public final String description;

  public static PermissionState identifiedBy(final PermissionId permissionId) {
    return new PermissionState(permissionId, new HashSet<>(), null, null);
  }

  public PermissionState(final PermissionId permissionId, final Set<Constraint> constraints, final String name, final String description) {
    this.id = permissionId;
    this.constraints.addAll(constraints);
    this.name = name;
    this.description = description;
  }

  public PermissionState provisionPermission(final String name, final String description) {
    //TODO: Implement command logic.
    return new PermissionState(this.id, this.constraints, name, description);
  }

  public PermissionState enforce(final Constraint constraint) {
    //TODO: Implement command logic.
    this.constraints.add(constraint);
    return new PermissionState(this.id, this.constraints, this.name, this.description);
  }

  public PermissionState enforceReplacement(final Constraint constraint) {
    //TODO: Implement command logic.
    this.constraints.add(constraint);
    return new PermissionState(this.id, this.constraints, this.name, this.description);
  }

  public PermissionState forget(final Constraint constraint) {
    //TODO: Implement command logic.
    this.constraints.add(constraint);
    return new PermissionState(this.id, this.constraints, this.name, this.description);
  }

  public PermissionState changeDescription(final String description) {
    //TODO: Implement command logic.
    return new PermissionState(this.id, this.constraints, this.name, description);
  }

}
