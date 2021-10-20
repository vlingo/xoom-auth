package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.auth.model.value.Constraint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PermissionState {

  public final PermissionId id;
  public final Set<Constraint> constraints;
  public final String name;
  public final String description;

  public static PermissionState identifiedBy(final PermissionId permissionId) {
    return new PermissionState(permissionId, new HashSet<>(), null, null);
  }

  public PermissionState(final PermissionId permissionId, final Set<Constraint> constraints, final String name, final String description) {
    this.id = permissionId;
    this.constraints = Collections.unmodifiableSet(constraints);
    this.name = name;
    this.description = description;
  }

  public PermissionState provisionPermission(final String name, final String description) {
    return new PermissionState(this.id, this.constraints, name, description);
  }

  public PermissionState enforce(final Constraint constraint) {
    return new PermissionState(this.id, includeConstraint(constraints, constraint), this.name, this.description);
  }

  public PermissionState enforceReplacement(final String constraintName, final Constraint constraint) {
    Set<Constraint> updatedConstraints = constraintOf(constraintName)
            .map(c -> removeConstraint(this.constraints, c))
            .map(c -> includeConstraint(c, constraint))
            .orElse(this.constraints);
    return new PermissionState(this.id, updatedConstraints, this.name, this.description);
  }

  public PermissionState forget(final String constraintName) {
    Set<Constraint> updatedConstraints = constraintOf(constraintName)
            .map(c -> removeConstraint(this.constraints, c))
            .orElse(this.constraints);
    return new PermissionState(this.id, updatedConstraints, this.name, this.description);
  }

  public PermissionState changeDescription(final String description) {
    return new PermissionState(this.id, this.constraints, this.name, description);
  }

  private Optional<Constraint> constraintOf(final String constraintName) {
    return this.constraints.stream()
            .filter(c -> c.name.equals(constraintName))
            .findFirst();
  }

  private Set<Constraint> includeConstraint(final Set<Constraint> constraints, final Constraint constraint) {
    return Stream.concat(constraints.stream(), Stream.of(constraint)).collect(Collectors.toSet());
  }

  private Set<Constraint> removeConstraint(final Set<Constraint> constraints, final Constraint constraint) {
    return constraints.stream().filter(c -> !c.equals(constraint)).collect(Collectors.toSet());
  }
}
