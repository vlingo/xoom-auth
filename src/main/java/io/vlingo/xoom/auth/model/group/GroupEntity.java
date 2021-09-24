package io.vlingo.xoom.auth.model.group;

import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.lattice.model.sourcing.EventSourced;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#sourced">EventSourced</a>
 */
public final class GroupEntity extends EventSourced implements Group {
  private GroupState state;

  public GroupEntity(final String id) {
    super(id);
    this.state = GroupState.identifiedBy(id);
  }

  static {
    EventSourced.registerConsumer(GroupEntity.class, GroupProvisioned.class, GroupEntity::applyGroupProvisioned);
    EventSourced.registerConsumer(GroupEntity.class, GroupDescriptionChanged.class, GroupEntity::applyGroupDescriptionChanged);
    EventSourced.registerConsumer(GroupEntity.class, GroupAssignedToGroup.class, GroupEntity::applyGroupAssignedToGroup);
    EventSourced.registerConsumer(GroupEntity.class, GroupUnassignedFromGroup.class, GroupEntity::applyGroupUnassignedFromGroup);
    EventSourced.registerConsumer(GroupEntity.class, UserAssignedToGroup.class, GroupEntity::applyUserAssignedToGroup);
    EventSourced.registerConsumer(GroupEntity.class, UserUnassignedFromGroup.class, GroupEntity::applyUserUnassignedFromGroup);
  }

  @Override
  public Completes<GroupState> provisionGroup(final String name, final String description, final String tenantId) {
    /**
     * TODO: Implement command logic. See {@link GroupState#provisionGroup()}
     */
    return apply(new GroupProvisioned(state.id, name, description, tenantId), () -> state);
  }

  @Override
  public Completes<GroupState> changeDescription(final String description, final String tenantId) {
    /**
     * TODO: Implement command logic. See {@link GroupState#changeDescription()}
     */
    return apply(new GroupDescriptionChanged(state.id, description, tenantId), () -> state);
  }

  @Override
  public Completes<GroupState> assignGroup(final String tenantId) {
    /**
     * TODO: Implement command logic. See {@link GroupState#assignGroup()}
     */
    return apply(new GroupAssignedToGroup(state.id, tenantId), () -> state);
  }

  @Override
  public Completes<GroupState> unassignGroup(final String tenantId) {
    /**
     * TODO: Implement command logic. See {@link GroupState#unassignGroup()}
     */
    return apply(new GroupUnassignedFromGroup(state.id, tenantId), () -> state);
  }

  @Override
  public Completes<GroupState> assignUser(final String tenantId) {
    /**
     * TODO: Implement command logic. See {@link GroupState#assignUser()}
     */
    return apply(new UserAssignedToGroup(state.id, tenantId), () -> state);
  }

  @Override
  public Completes<GroupState> unassignUser(final String tenantId) {
    /**
     * TODO: Implement command logic. See {@link GroupState#unassignUser()}
     */
    return apply(new UserUnassignedFromGroup(state.id, tenantId), () -> state);
  }

  private void applyGroupProvisioned(final GroupProvisioned event) {
    state = state.provisionGroup(event.name, event.description, event.tenantId);
  }

  private void applyGroupDescriptionChanged(final GroupDescriptionChanged event) {
    state = state.changeDescription(event.description, event.tenantId);
  }

  private void applyGroupAssignedToGroup(final GroupAssignedToGroup event) {
    state = state.assignGroup(event.tenantId);
  }

  private void applyGroupUnassignedFromGroup(final GroupUnassignedFromGroup event) {
    state = state.unassignGroup(event.tenantId);
  }

  private void applyUserAssignedToGroup(final UserAssignedToGroup event) {
    state = state.assignUser(event.tenantId);
  }

  private void applyUserUnassignedFromGroup(final UserUnassignedFromGroup event) {
    state = state.unassignUser(event.tenantId);
  }

  /*
   * Restores my initial state by means of {@code state}.
   *
   * @param snapshot the {@code GroupState} holding my state
   * @param currentVersion the int value of my current version; may be helpful in determining if snapshot is needed
   */
  @Override
  @SuppressWarnings("hiding")
  protected <GroupState> void restoreSnapshot(final GroupState snapshot, final int currentVersion) {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
  }

  /*
   * Answer the valid {@code GroupState} instance if a snapshot should
   * be taken and persisted along with applied {@code Source<T>} instance(s).
   *
   * @return GroupState
   */
  @Override
  @SuppressWarnings("unchecked")
  protected GroupState snapshot() {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
    return null;
  }
}