package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

import io.vlingo.xoom.auth.model.value.*;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class PermissionConstraintForgotten extends IdentifiedDomainEvent {

  public final PermissionId permissionId;
  public final Constraint constraint;

  public PermissionConstraintForgotten(final PermissionId permissionId, final Constraint constraint) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.permissionId = permissionId;
    this.constraint = constraint;
  }

  @Override
  public String identity() {
    return permissionId.idString();
  }
}
