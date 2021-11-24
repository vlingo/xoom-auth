package io.vlingo.xoom.auth.model.tenant;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class TenantDescriptionChanged extends IdentifiedDomainEvent {

  public final TenantId tenantId;
  public final String description;

  public TenantDescriptionChanged(final TenantId tenantId, final String description) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.tenantId = tenantId;
    this.description = description;
  }

  @Override
  public String identity() {
    return tenantId.idString();
  }
}
