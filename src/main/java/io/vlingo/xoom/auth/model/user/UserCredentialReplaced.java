package io.vlingo.xoom.auth.model.user;

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
public final class UserCredentialReplaced extends IdentifiedDomainEvent {

  public final String id;
  public final Credential credential;

  public UserCredentialReplaced(final String id, final Credential credential) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
    this.credential = credential;
  }

  @Override
  public String identity() {
    return id;
  }
}
