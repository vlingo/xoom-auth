package io.vlingo.xoom.auth.model.tenant;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class TenantActivated extends IdentifiedDomainEvent {

  public final String id;

  public TenantActivated(final String id) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
  }

  @Override
  public String identity() {
    return id;
  }
}