package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

import io.vlingo.xoom.auth.model.value.*;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class UserProfileReplaced extends IdentifiedDomainEvent {

  public final String id;
  public final String tenantId;
  public final String username;
  public final Profile profile;

  public UserProfileReplaced(final String id, final String tenantId, final String username, final Profile profile) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
    this.tenantId = tenantId;
    this.username = username;
    this.profile = profile;
  }

  @Override
  public String identity() {
    return id;
  }
}
