package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.common.Completes;

public interface Role {

  Completes<RoleState> provisionRole(final String name, final String description);

  Completes<RoleState> changeDescription(final String description);

  Completes<RoleState> assignGroup(final GroupId groupId);

  Completes<RoleState> unassignGroup(final GroupId groupId);

  Completes<RoleState> assignUser(final UserId userId);

  Completes<RoleState> unassignUser(final UserId userId);

  Completes<RoleState> attach(final PermissionId permissionId);

  Completes<RoleState> detach(final PermissionId permissionId);

}