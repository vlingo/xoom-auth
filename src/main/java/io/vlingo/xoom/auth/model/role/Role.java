package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.actors.Stage;

public interface Role {

  Completes<RoleState> provisionRole(final String tenantId, final String name, final String description);

  static Completes<RoleState> provisionRole(final Stage stage, final String tenantId, final String name, final String description) {
    final io.vlingo.xoom.actors.Address _address = stage.addressFactory().uniquePrefixedWith("g-");
    final Role _role = stage.actorFor(Role.class, Definition.has(RoleEntity.class, Definition.parameters(_address.idString())), _address);
    return _role.provisionRole(tenantId, name, description);
  }

  Completes<RoleState> changeDescription(final String tenantId, final String name, final String description);

  Completes<RoleState> assignGroup(final String tenantId, final String name);

  Completes<RoleState> unassignGroup(final String tenantId, final String name);

  Completes<RoleState> assignUser(final String tenantId, final String name);

  Completes<RoleState> unassignUser(final String tenantId, final String name);

  Completes<RoleState> attach(final String tenantId, final String name);

  Completes<RoleState> detach(final String tenantId, final String name);

}