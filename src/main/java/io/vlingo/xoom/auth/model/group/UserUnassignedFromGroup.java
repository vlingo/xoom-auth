package io.vlingo.xoom.auth.model.group;

import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class UserUnassignedFromGroup extends IdentifiedDomainEvent {

  public final GroupId groupId;
  public final UserId userId;

  public UserUnassignedFromGroup(final GroupId groupId, final UserId userId) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.groupId = groupId;
    this.userId = userId;
  }

  @Override
  public String identity() {
    return groupId.idString();
  }
}
