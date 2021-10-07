package io.vlingo.xoom.auth.model.group;


public final class GroupState {

  public final GroupId id;
  public final String name;
  public final String description;

  public static GroupState identifiedBy(final GroupId groupId) {
    return new GroupState(groupId, null, null);
  }

  public GroupState(final GroupId groupId, final String name, final String description) {
    this.id = groupId;
    this.name = name;
    this.description = description;
  }

  public GroupState provisionGroup(final String name, final String description) {
    //TODO: Implement command logic.
    return new GroupState(this.id, name, description);
  }

  public GroupState changeDescription(final String description) {
    //TODO: Implement command logic.
    return new GroupState(this.id, this.name, description);
  }

  public GroupState assignGroup(final GroupId innerGroupId) {
    //TODO: Implement command logic.
    return new GroupState(this.id, this.name, this.description);
  }

  public GroupState unassignGroup(final GroupId innerGroupId) {
    //TODO: Implement command logic.
    return new GroupState(this.id, this.name, this.description);
  }

  public GroupState assignUser(final String tenantId) {
    //TODO: Implement command logic.
    return new GroupState(this.id, this.name, this.description);
  }

  public GroupState unassignUser(final String tenantId) {
    //TODO: Implement command logic.
    return new GroupState(this.id, this.name, this.description);
  }

}
