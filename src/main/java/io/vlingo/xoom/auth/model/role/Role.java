package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.common.Completes;

public interface Role {

  Completes<RoleState> provisionRole(final String name, final String description);

  Completes<RoleState> changeDescription(final String description);

  Completes<RoleState> assignGroup(final String name);

  Completes<RoleState> unassignGroup(final String name);

  Completes<RoleState> assignUser(final String name);

  Completes<RoleState> unassignUser(final String name);

  Completes<RoleState> attach(final String name);

  Completes<RoleState> detach(final String name);

}