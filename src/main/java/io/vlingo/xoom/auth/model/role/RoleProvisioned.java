package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class RoleProvisioned extends IdentifiedDomainEvent {

  public final RoleId roleId;
  public final String name;
  public final String description;

  public RoleProvisioned(final RoleId roleId, final String name, final String description) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.roleId = roleId;
    this.name = name;
    this.description = description;
  }

  @Override
  public String identity() {
    return roleId.idString();
  }
}
