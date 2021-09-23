package io.vlingo.xoom.auth.model.permission;

import java.util.*;
import io.vlingo.xoom.auth.model.value.*;

public final class PermissionState {

  public final String id;
  public final Set<Constraint> constraints = new HashSet<>();
  public final String description;
  public final String name;
  public final String tenantId;

  public static PermissionState identifiedBy(final String id) {
    return new PermissionState(id, new HashSet<>(), null, null, null);
  }

  public PermissionState (final String id, final Set<Constraint> constraints, final String description, final String name, final String tenantId) {
    this.id = id;
    this.constraints.addAll(constraints);
    this.description = description;
    this.name = name;
    this.tenantId = tenantId;
  }

  public PermissionState provisionPermission(final String description, final String name, final String tenantId) {
    //TODO: Implement command logic.
    return new PermissionState(this.id, this.constraints, description, name, tenantId);
  }

  public PermissionState enforce(final Constraint constraint) {
    //TODO: Implement command logic.
    this.constraints.add(constraint);
    return new PermissionState(this.id, this.constraints, this.description, this.name, this.tenantId);
  }

  public PermissionState enforceReplacement(final Constraint constraint) {
    //TODO: Implement command logic.
    this.constraints.add(constraint);
    return new PermissionState(this.id, this.constraints, this.description, this.name, this.tenantId);
  }

  public PermissionState forget(final Constraint constraint) {
    //TODO: Implement command logic.
    this.constraints.add(constraint);
    return new PermissionState(this.id, this.constraints, this.description, this.name, this.tenantId);
  }

  public PermissionState changeDescription(final String description, final String tenantId) {
    //TODO: Implement command logic.
    return new PermissionState(this.id, this.constraints, description, this.name, tenantId);
  }

}
