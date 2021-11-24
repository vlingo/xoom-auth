package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class RolePermissionDetached extends IdentifiedDomainEvent {

  public final RoleId roleId;
  public final PermissionId permissionId;

  public RolePermissionDetached(final RoleId roleId, final PermissionId permissionId) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.roleId = roleId;
    this.permissionId = permissionId;
  }

  @Override
  public String identity() {
    return roleId.idString();
  }
}
