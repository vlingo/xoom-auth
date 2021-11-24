package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class UserDeactivated extends IdentifiedDomainEvent {

  public final UserId userId;

  public UserDeactivated(final UserId userId) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.userId = userId;
  }

  @Override
  public String identity() {
    return userId.idString();
  }
}
