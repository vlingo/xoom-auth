package io.vlingo.xoom.auth.model.group;


public final class GroupState {

  public final String id;
  public final String name;
  public final String description;
  public final String tenantId;

  public static GroupState identifiedBy(final String id) {
    return new GroupState(id, null, null, null);
  }

  public GroupState (final String id, final String name, final String description, final String tenantId) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.tenantId = tenantId;
  }

  public GroupState provisionGroup(final String name, final String description, final String tenantId) {
    //TODO: Implement command logic.
    return new GroupState(this.id, name, description, tenantId);
  }

  public GroupState changeDescription(final String description, final String tenantId) {
    //TODO: Implement command logic.
    return new GroupState(this.id, this.name, description, tenantId);
  }

  public GroupState assignGroup(final String id, final String tenantId) {
    //TODO: Implement command logic.
    return new GroupState(id, this.name, this.description, tenantId);
  }

  public GroupState unassignGroup(final String id, final String tenantId) {
    //TODO: Implement command logic.
    return new GroupState(id, this.name, this.description, tenantId);
  }

  public GroupState assignUser(final String id, final String tenantId) {
    //TODO: Implement command logic.
    return new GroupState(id, this.name, this.description, tenantId);
  }

  public GroupState unassignUser(final String id, final String tenantId) {
    //TODO: Implement command logic.
    return new GroupState(id, this.name, this.description, tenantId);
  }

}
