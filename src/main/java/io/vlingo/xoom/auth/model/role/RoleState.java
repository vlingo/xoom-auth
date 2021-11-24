package io.vlingo.xoom.auth.model.role;


import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.auth.model.user.UserId;

public final class RoleState {

  public final RoleId roleId;
  public final String name;
  public final String description;

  public static RoleState identifiedBy(final RoleId roleId) {
    return new RoleState(roleId, null, null);
  }

  public RoleState(final RoleId roleId, final String name, final String description) {
    this.roleId = roleId;
    this.name = name;
    this.description = description;
  }

  public RoleState provisionRole(final String name, final String description) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, name, description);
  }

  public RoleState changeDescription(final String description) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, this.name, description);
  }

  public RoleState assignGroup(final GroupId groupId) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId,this.name, this.description);
  }

  public RoleState unassignGroup(final GroupId groupId) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, this.name, this.description);
  }

  public RoleState assignUser(final UserId userId) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, this.name, this.description);
  }

  public RoleState unassignUser(final UserId userId) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, this.name, this.description);
  }

  public RoleState attach(final PermissionId permissionId) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, this.name, this.description);
  }

  public RoleState detach(final PermissionId permissionId) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, this.name, this.description);
  }

}
