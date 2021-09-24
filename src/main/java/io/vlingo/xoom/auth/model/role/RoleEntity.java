package io.vlingo.xoom.auth.model.role;

import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.lattice.model.sourcing.EventSourced;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#sourced">EventSourced</a>
 */
public final class RoleEntity extends EventSourced implements Role {
  private RoleState state;

  public RoleEntity(final String id) {
    super(id);
    this.state = RoleState.identifiedBy(id);
  }

  static {
    EventSourced.registerConsumer(RoleEntity.class, RoleProvisioned.class, RoleEntity::applyRoleProvisioned);
    EventSourced.registerConsumer(RoleEntity.class, RoleDescriptionChanged.class, RoleEntity::applyRoleDescriptionChanged);
    EventSourced.registerConsumer(RoleEntity.class, GroupAssignedToRole.class, RoleEntity::applyGroupAssignedToRole);
    EventSourced.registerConsumer(RoleEntity.class, GroupUnassignedFromRole.class, RoleEntity::applyGroupUnassignedFromRole);
    EventSourced.registerConsumer(RoleEntity.class, UserAssignedToRole.class, RoleEntity::applyUserAssignedToRole);
    EventSourced.registerConsumer(RoleEntity.class, UserUnassignedFromRole.class, RoleEntity::applyUserUnassignedFromRole);
    EventSourced.registerConsumer(RoleEntity.class, RolePermissionAttached.class, RoleEntity::applyRolePermissionAttached);
    EventSourced.registerConsumer(RoleEntity.class, RolePermissionDetached.class, RoleEntity::applyRolePermissionDetached);
  }

  @Override
  public Completes<RoleState> provisionRole(final String tenantId, final String name, final String description) {
    /**
     * TODO: Implement command logic. See {@link RoleState#provisionRole()}
     */
    return apply(new RoleProvisioned(state.id, tenantId, name, description), () -> state);
  }

  @Override
  public Completes<RoleState> changeDescription(final String tenantId, final String name, final String description) {
    /**
     * TODO: Implement command logic. See {@link RoleState#changeDescription()}
     */
    return apply(new RoleDescriptionChanged(state.id, tenantId, name, description), () -> state);
  }

  @Override
  public Completes<RoleState> assignGroup(final String tenantId, final String name) {
    /**
     * TODO: Implement command logic. See {@link RoleState#assignGroup()}
     */
    return apply(new GroupAssignedToRole(state.id, tenantId, name), () -> state);
  }

  @Override
  public Completes<RoleState> unassignGroup(final String tenantId, final String name) {
    /**
     * TODO: Implement command logic. See {@link RoleState#unassignGroup()}
     */
    return apply(new GroupUnassignedFromRole(state.id, tenantId, name), () -> state);
  }

  @Override
  public Completes<RoleState> assignUser(final String tenantId, final String name) {
    /**
     * TODO: Implement command logic. See {@link RoleState#assignUser()}
     */
    return apply(new UserAssignedToRole(state.id, tenantId, name), () -> state);
  }

  @Override
  public Completes<RoleState> unassignUser(final String tenantId, final String name) {
    /**
     * TODO: Implement command logic. See {@link RoleState#unassignUser()}
     */
    return apply(new UserUnassignedFromRole(state.id, tenantId, name), () -> state);
  }

  @Override
  public Completes<RoleState> attach(final String tenantId, final String name) {
    /**
     * TODO: Implement command logic. See {@link RoleState#attach()}
     */
    return apply(new RolePermissionAttached(state.id, tenantId, name), () -> state);
  }

  @Override
  public Completes<RoleState> detach(final String tenantId, final String name) {
    /**
     * TODO: Implement command logic. See {@link RoleState#detach()}
     */
    return apply(new RolePermissionDetached(state.id, tenantId, name), () -> state);
  }

  private void applyRoleProvisioned(final RoleProvisioned event) {
    state = state.provisionRole(event.tenantId, event.name, event.description);
  }

  private void applyRoleDescriptionChanged(final RoleDescriptionChanged event) {
    state = state.changeDescription(event.tenantId, event.name, event.description);
  }

  private void applyGroupAssignedToRole(final GroupAssignedToRole event) {
    state = state.assignGroup(event.tenantId, event.name);
  }

  private void applyGroupUnassignedFromRole(final GroupUnassignedFromRole event) {
    state = state.unassignGroup(event.tenantId, event.name);
  }

  private void applyUserAssignedToRole(final UserAssignedToRole event) {
    state = state.assignUser(event.tenantId, event.name);
  }

  private void applyUserUnassignedFromRole(final UserUnassignedFromRole event) {
    state = state.unassignUser(event.tenantId, event.name);
  }

  private void applyRolePermissionAttached(final RolePermissionAttached event) {
    state = state.attach(event.tenantId, event.name);
  }

  private void applyRolePermissionDetached(final RolePermissionDetached event) {
    state = state.detach(event.tenantId, event.name);
  }

  /*
   * Restores my initial state by means of {@code state}.
   *
   * @param snapshot the {@code RoleState} holding my state
   * @param currentVersion the int value of my current version; may be helpful in determining if snapshot is needed
   */
  @Override
  @SuppressWarnings("hiding")
  protected <RoleState> void restoreSnapshot(final RoleState snapshot, final int currentVersion) {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
  }

  /*
   * Answer the valid {@code RoleState} instance if a snapshot should
   * be taken and persisted along with applied {@code Source<T>} instance(s).
   *
   * @return RoleState
   */
  @Override
  @SuppressWarnings("unchecked")
  protected RoleState snapshot() {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
    return null;
  }
}