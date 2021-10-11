package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.auth.model.value.*;
import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.lattice.model.sourcing.EventSourced;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#sourced">EventSourced</a>
 */
public final class PermissionEntity extends EventSourced implements Permission {
  private PermissionState state;

  public PermissionEntity(final PermissionId permissionId) {
    super(permissionId.idString());
    this.state = PermissionState.identifiedBy(permissionId);
  }

  static {
    EventSourced.registerConsumer(PermissionEntity.class, PermissionProvisioned.class, PermissionEntity::applyPermissionProvisioned);
    EventSourced.registerConsumer(PermissionEntity.class, PermissionConstraintEnforced.class, PermissionEntity::applyPermissionConstraintEnforced);
    EventSourced.registerConsumer(PermissionEntity.class, PermissionConstraintReplacementEnforced.class, PermissionEntity::applyPermissionConstraintReplacementEnforced);
    EventSourced.registerConsumer(PermissionEntity.class, PermissionConstraintForgotten.class, PermissionEntity::applyPermissionConstraintForgotten);
    EventSourced.registerConsumer(PermissionEntity.class, PermissionDescriptionChanged.class, PermissionEntity::applyPermissionDescriptionChanged);
  }

  @Override
  public Completes<PermissionState> provisionPermission(final String name, final String description) {
    /**
     * TODO: Implement command logic. See {@link PermissionState#provisionPermission()}
     */
    return apply(new PermissionProvisioned(state.id, name, description), () -> state);
  }

  @Override
  public Completes<PermissionState> enforce(final Constraint constraint) {
    /**
     * TODO: Implement command logic. See {@link PermissionState#enforce()}
     */
    return apply(new PermissionConstraintEnforced(state.id, constraint), () -> state);
  }

  @Override
  public Completes<PermissionState> enforceReplacement(final Constraint constraint) {
    /**
     * TODO: Implement command logic. See {@link PermissionState#enforceReplacement()}
     */
    return apply(new PermissionConstraintReplacementEnforced(state.id, constraint), () -> state);
  }

  @Override
  public Completes<PermissionState> forget(final Constraint constraint) {
    /**
     * TODO: Implement command logic. See {@link PermissionState#forget()}
     */
    return apply(new PermissionConstraintForgotten(state.id, constraint), () -> state);
  }

  @Override
  public Completes<PermissionState> changeDescription(final String description) {
    /**
     * TODO: Implement command logic. See {@link PermissionState#changeDescription()}
     */
    return apply(new PermissionDescriptionChanged(state.id, description), () -> state);
  }

  private void applyPermissionProvisioned(final PermissionProvisioned event) {
    state = state.provisionPermission(event.name, event.description);
  }

  private void applyPermissionConstraintEnforced(final PermissionConstraintEnforced event) {
    state = state.enforce(event.constraint);
  }

  private void applyPermissionConstraintReplacementEnforced(final PermissionConstraintReplacementEnforced event) {
    state = state.enforceReplacement(event.constraint);
  }

  private void applyPermissionConstraintForgotten(final PermissionConstraintForgotten event) {
    state = state.forget(event.constraint);
  }

  private void applyPermissionDescriptionChanged(final PermissionDescriptionChanged event) {
    state = state.changeDescription(event.description);
  }

  /*
   * Restores my initial state by means of {@code state}.
   *
   * @param snapshot the {@code PermissionState} holding my state
   * @param currentVersion the int value of my current version; may be helpful in determining if snapshot is needed
   */
  @Override
  @SuppressWarnings("hiding")
  protected <PermissionState> void restoreSnapshot(final PermissionState snapshot, final int currentVersion) {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
  }

  /*
   * Answer the valid {@code PermissionState} instance if a snapshot should
   * be taken and persisted along with applied {@code Source<T>} instance(s).
   *
   * @return PermissionState
   */
  @Override
  @SuppressWarnings("unchecked")
  protected PermissionState snapshot() {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
    return null;
  }
}
