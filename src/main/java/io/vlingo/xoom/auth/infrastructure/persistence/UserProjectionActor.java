package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.Events;
import io.vlingo.xoom.auth.infrastructure.persistence.UserView.CredentialView;
import io.vlingo.xoom.auth.infrastructure.persistence.UserView.ProfileView;
import io.vlingo.xoom.auth.model.user.*;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.turbo.ComponentRegistry;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/projections#implementing-with-the-statestoreprojectionactor">
 *   StateStoreProjectionActor
 * </a>
 */
public class UserProjectionActor extends StateStoreProjectionActor<UserView> {

  private static final UserView Empty = UserView.empty();

  public UserProjectionActor() {
    this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
  }

  public UserProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected UserView currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected UserView merge(final UserView previousData, final int previousVersion, final UserView currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    UserView merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case UserRegistered: {
          final UserRegistered typedEvent = typed(event);
          final ProfileView profile = ProfileView.from(typedEvent.profile);
          final Set<CredentialView> credentials = typedEvent.credentials.stream().map(c -> CredentialView.from(c)).collect(Collectors.toSet());
          merged = UserView.from(typedEvent.userId, typedEvent.username, profile, credentials, typedEvent.active);
          break;
        }

        case UserActivated: {
          final UserActivated typedEvent = typed(event);
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, true);
          break;
        }

        case UserDeactivated: {
          final UserDeactivated typedEvent = typed(event);
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, false);
          break;
        }

        case UserCredentialAdded: {
          final UserCredentialAdded typedEvent = typed(event);
          final CredentialView credential = CredentialView.from(typedEvent.credential);
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, includeCredential(previousData.credentials, credential), previousData.active);
          break;
        }

        case UserCredentialRemoved: {
          final UserCredentialRemoved typedEvent = typed(event);
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, removeCredential(previousData.credentials, typedEvent.authority), previousData.active);
          break;
        }

        case UserCredentialReplaced: {
          final UserCredentialReplaced typedEvent = typed(event);
          final CredentialView credential = CredentialView.from(typedEvent.credential);
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, includeCredential(removeCredential(previousData.credentials, typedEvent.authority), credential), previousData.active);
          break;
        }

        case UserProfileReplaced: {
          final UserProfileReplaced typedEvent = typed(event);
          final ProfileView profile = ProfileView.from(typedEvent.profile);
          merged = UserView.from(typedEvent.userId, previousData.username, profile, previousData.credentials, previousData.active);
          break;
        }

        default:
          logger().warn("Event of type " + event.typeName() + " was not matched.");
          break;
      }
    }

    return merged;
  }

  private Set<CredentialView> includeCredential(final Set<CredentialView> credentials, final CredentialView credential) {
    return Stream.concat(credentials.stream(), Stream.of(credential)).collect(Collectors.toSet());
  }

  private Set<CredentialView> removeCredential(final Set<CredentialView> credentials, final String authority) {
    return credentials.stream().filter(c -> !c.authority.equals(authority)).collect(Collectors.toSet());
  }
}
