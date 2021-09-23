package io.vlingo.xoom.auth.model.tenant;

import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.lattice.model.sourcing.EventSourced;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#sourced">EventSourced</a>
 */
public final class TenantEntity extends EventSourced implements Tenant {
  private TenantState state;

  public TenantEntity(final String id) {
    super(id);
    this.state = TenantState.identifiedBy(id);
  }

  static {
    EventSourced.registerConsumer(TenantEntity.class, TenantSubscribed.class, TenantEntity::applyTenantSubscribed);
    EventSourced.registerConsumer(TenantEntity.class, TenantActivated.class, TenantEntity::applyTenantActivated);
    EventSourced.registerConsumer(TenantEntity.class, TenantDeactivated.class, TenantEntity::applyTenantDeactivated);
    EventSourced.registerConsumer(TenantEntity.class, TenantNameChanged.class, TenantEntity::applyTenantNameChanged);
    EventSourced.registerConsumer(TenantEntity.class, TenantDescriptionChanged.class, TenantEntity::applyTenantDescriptionChanged);
  }

  @Override
  public Completes<TenantState> subscribeFor() {
    /**
     * TODO: Implement command logic. See {@link TenantState#subscribeFor()}
     */
    return apply(new TenantSubscribed(state.id), () -> state);
  }

  @Override
  public Completes<TenantState> activate(final String id) {
    /**
     * TODO: Implement command logic. See {@link TenantState#activate()}
     */
    return apply(new TenantActivated(state.id, id), () -> state);
  }

  @Override
  public Completes<TenantState> deactivate(final String id) {
    /**
     * TODO: Implement command logic. See {@link TenantState#deactivate()}
     */
    return apply(new TenantDeactivated(state.id, id), () -> state);
  }

  @Override
  public Completes<TenantState> changeName(final String id, final String name) {
    /**
     * TODO: Implement command logic. See {@link TenantState#changeName()}
     */
    return apply(new TenantNameChanged(state.id, id, name), () -> state);
  }

  @Override
  public Completes<TenantState> changeDescription(final String id, final String description) {
    /**
     * TODO: Implement command logic. See {@link TenantState#changeDescription()}
     */
    return apply(new TenantDescriptionChanged(state.id, id, description), () -> state);
  }

  private void applyTenantSubscribed(final TenantSubscribed event) {
    state = state.subscribeFor();
  }

  private void applyTenantActivated(final TenantActivated event) {
    state = state.activate(event.id);
  }

  private void applyTenantDeactivated(final TenantDeactivated event) {
    state = state.deactivate(event.id);
  }

  private void applyTenantNameChanged(final TenantNameChanged event) {
    state = state.changeName(event.id, event.name);
  }

  private void applyTenantDescriptionChanged(final TenantDescriptionChanged event) {
    state = state.changeDescription(event.id, event.description);
  }

  /*
   * Restores my initial state by means of {@code state}.
   *
   * @param snapshot the {@code TenantState} holding my state
   * @param currentVersion the int value of my current version; may be helpful in determining if snapshot is needed
   */
  @Override
  @SuppressWarnings("hiding")
  protected <TenantState> void restoreSnapshot(final TenantState snapshot, final int currentVersion) {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
  }

  /*
   * Answer the valid {@code TenantState} instance if a snapshot should
   * be taken and persisted along with applied {@code Source<T>} instance(s).
   *
   * @return TenantState
   */
  @Override
  @SuppressWarnings("unchecked")
  protected TenantState snapshot() {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
    return null;
  }
}
