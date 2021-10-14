package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class PermissionConstraintForgotten extends IdentifiedDomainEvent {

  public final PermissionId permissionId;
  public final String constraintName;

  public PermissionConstraintForgotten(final PermissionId permissionId, final String constraintName) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.permissionId = permissionId;
    this.constraintName = constraintName;
  }

  @Override
  public String identity() {
    return permissionId.idString();
  }
}
