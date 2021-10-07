package io.vlingo.xoom.auth.model.group;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class GroupUnassignedFromGroup extends IdentifiedDomainEvent {

  public final GroupId groupId;
  public final GroupId innerGroupId;

  public GroupUnassignedFromGroup(final GroupId groupId, final GroupId innerGroupId) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.groupId = groupId;
    this.innerGroupId = innerGroupId;
  }

  @Override
  public String identity() {
    return groupId.idString();
  }
}
