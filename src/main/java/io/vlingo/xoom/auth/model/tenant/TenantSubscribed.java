package io.vlingo.xoom.auth.model.tenant;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class TenantSubscribed extends IdentifiedDomainEvent {

  public final TenantId tenantId;
  public final String name;
  public final String description;
  public final boolean active;

  public TenantSubscribed(final TenantId tenantId, final String name, final String description, final boolean active) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.active = active;
  }

  @Override
  public String identity() {
    return tenantId.idString();
  }
}
