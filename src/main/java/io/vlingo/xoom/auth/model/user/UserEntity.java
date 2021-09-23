package io.vlingo.xoom.auth.model.user;

import java.util.*;
import io.vlingo.xoom.auth.model.value.*;
import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.lattice.model.sourcing.EventSourced;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#sourced">EventSourced</a>
 */
public final class UserEntity extends EventSourced implements User {
  private UserState state;

  public UserEntity(final String id) {
    super(id);
    this.state = UserState.identifiedBy(id);
  }

  static {
    EventSourced.registerConsumer(UserEntity.class, UserRegistered.class, UserEntity::applyUserRegistered);
    EventSourced.registerConsumer(UserEntity.class, UserActivated.class, UserEntity::applyUserActivated);
    EventSourced.registerConsumer(UserEntity.class, UserDeactivated.class, UserEntity::applyUserDeactivated);
    EventSourced.registerConsumer(UserEntity.class, UserCredentialAdded.class, UserEntity::applyUserCredentialAdded);
    EventSourced.registerConsumer(UserEntity.class, UserCredentialRemoved.class, UserEntity::applyUserCredentialRemoved);
    EventSourced.registerConsumer(UserEntity.class, UserCredentialReplaced.class, UserEntity::applyUserCredentialReplaced);
    EventSourced.registerConsumer(UserEntity.class, UserProfileReplaced.class, UserEntity::applyUserProfileReplaced);
  }

  @Override
  public Completes<UserState> registerUser(final String tenantId, final String username, final boolean active, final Profile profile) {
    /**
     * TODO: Implement command logic. See {@link UserState#registerUser()}
     */
    return apply(new UserRegistered(state.id, tenantId, username, active, profile), () -> state);
  }

  @Override
  public Completes<UserState> activate(final String tenantId, final String username) {
    /**
     * TODO: Implement command logic. See {@link UserState#activate()}
     */
    return apply(new UserActivated(state.id, tenantId, username), () -> state);
  }

  @Override
  public Completes<UserState> deactivate(final String tenantId, final String username) {
    /**
     * TODO: Implement command logic. See {@link UserState#deactivate()}
     */
    return apply(new UserDeactivated(state.id, tenantId, username), () -> state);
  }

  @Override
  public Completes<UserState> addCredential(final Credential credential) {
    /**
     * TODO: Implement command logic. See {@link UserState#addCredential()}
     */
    return apply(new UserCredentialAdded(state.id, credential), () -> state);
  }

  @Override
  public Completes<UserState> removeCredential(final Credential credential) {
    /**
     * TODO: Implement command logic. See {@link UserState#removeCredential()}
     */
    return apply(new UserCredentialRemoved(state.id, credential), () -> state);
  }

  @Override
  public Completes<UserState> replaceCredential(final Credential credential) {
    /**
     * TODO: Implement command logic. See {@link UserState#replaceCredential()}
     */
    return apply(new UserCredentialReplaced(state.id, credential), () -> state);
  }

  @Override
  public Completes<UserState> replaceProfile(final String tenantId, final String username, final Profile profile) {
    /**
     * TODO: Implement command logic. See {@link UserState#replaceProfile()}
     */
    return apply(new UserProfileReplaced(state.id, tenantId, username, profile), () -> state);
  }

  private void applyUserRegistered(final UserRegistered event) {
    state = state.registerUser(event.tenantId, event.username, event.active, event.profile);
  }

  private void applyUserActivated(final UserActivated event) {
    state = state.activate(event.tenantId, event.username);
  }

  private void applyUserDeactivated(final UserDeactivated event) {
    state = state.deactivate(event.tenantId, event.username);
  }

  private void applyUserCredentialAdded(final UserCredentialAdded event) {
    state = state.addCredential(event.credential);
  }

  private void applyUserCredentialRemoved(final UserCredentialRemoved event) {
    state = state.removeCredential(event.credential);
  }

  private void applyUserCredentialReplaced(final UserCredentialReplaced event) {
    state = state.replaceCredential(event.credential);
  }

  private void applyUserProfileReplaced(final UserProfileReplaced event) {
    state = state.replaceProfile(event.tenantId, event.username, event.profile);
  }

  /*
   * Restores my initial state by means of {@code state}.
   *
   * @param snapshot the {@code UserState} holding my state
   * @param currentVersion the int value of my current version; may be helpful in determining if snapshot is needed
   */
  @Override
  @SuppressWarnings("hiding")
  protected <UserState> void restoreSnapshot(final UserState snapshot, final int currentVersion) {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
  }

  /*
   * Answer the valid {@code UserState} instance if a snapshot should
   * be taken and persisted along with applied {@code Source<T>} instance(s).
   *
   * @return UserState
   */
  @Override
  @SuppressWarnings("unchecked")
  protected UserState snapshot() {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
    return null;
  }
}
