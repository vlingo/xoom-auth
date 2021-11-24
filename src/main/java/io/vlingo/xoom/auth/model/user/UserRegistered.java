package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.auth.model.value.Credential;
import io.vlingo.xoom.auth.model.value.Profile;
import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

import java.util.Set;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class UserRegistered extends IdentifiedDomainEvent {

  public final UserId userId;
  public final String username;
  public final boolean active;
  public final Set<Credential> credentials;
  public final Profile profile;

  public UserRegistered(final UserId userId, final String username, final Profile profile, final Set<Credential> credentials, final boolean active) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.userId = userId;
    this.username = username;
    this.active = active;
    this.credentials = credentials;
    this.profile = profile;
  }

  @Override
  public String identity() {
    return userId.idString();
  }
}
