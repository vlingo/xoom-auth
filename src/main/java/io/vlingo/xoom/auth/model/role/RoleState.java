package io.vlingo.xoom.auth.model.role;


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

  public RoleState assignGroup(final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, name, this.description);
  }

  public RoleState unassignGroup(final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, name, this.description);
  }

  public RoleState assignUser(final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, name, this.description);
  }

  public RoleState unassignUser(final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, name, this.description);
  }

  public RoleState attach(final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, name, this.description);
  }

  public RoleState detach(final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.roleId, name, this.description);
  }

}
