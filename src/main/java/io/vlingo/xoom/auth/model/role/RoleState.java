package io.vlingo.xoom.auth.model.role;


public final class RoleState {

  public final String id;
  public final String tenantId;
  public final String name;
  public final String description;

  public static RoleState identifiedBy(final String id) {
    return new RoleState(id, null, null, null);
  }

  public RoleState (final String id, final String tenantId, final String name, final String description) {
    this.id = id;
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
  }

  public RoleState provisionRole(final String tenantId, final String name, final String description) {
    //TODO: Implement command logic.
    return new RoleState(this.id, tenantId, name, description);
  }

  public RoleState changeDescription(final String tenantId, final String name, final String description) {
    //TODO: Implement command logic.
    return new RoleState(this.id, tenantId, name, description);
  }

  public RoleState assignGroup(final String tenantId, final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.id, tenantId, name, this.description);
  }

  public RoleState unassignGroup(final String tenantId, final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.id, tenantId, name, this.description);
  }

  public RoleState assignUser(final String tenantId, final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.id, tenantId, name, this.description);
  }

  public RoleState unassignUser(final String tenantId, final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.id, tenantId, name, this.description);
  }

  public RoleState attach(final String tenantId, final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.id, tenantId, name, this.description);
  }

  public RoleState detach(final String tenantId, final String name) {
    //TODO: Implement command logic.
    return new RoleState(this.id, tenantId, name, this.description);
  }

}
