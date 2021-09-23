package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

import java.util.*;
import io.vlingo.xoom.auth.model.value.*;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class PermissionConstraintEnforced extends IdentifiedDomainEvent {

  public final String id;
  public final Constraint constraint;
  public final String tenantId;

  public PermissionConstraintEnforced(final String id, final Constraint constraint, final String tenantId) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
    this.constraint = constraint;
    this.tenantId = tenantId;
  }

  @Override
  public String identity() {
    return id;
  }
}
