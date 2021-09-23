package io.vlingo.xoom.auth.model.group;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class GroupProvisioned extends IdentifiedDomainEvent {

  public final String id;
  public final String name;
  public final String description;
  public final String tenantId;

  public GroupProvisioned(final String id, final String name, final String description, final String tenantId) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
    this.name = name;
    this.description = description;
    this.tenantId = tenantId;
  }

  @Override
  public String identity() {
    return id;
  }
}
