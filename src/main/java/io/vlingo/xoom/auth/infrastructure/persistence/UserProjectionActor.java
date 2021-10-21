package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.CredentialData;
import io.vlingo.xoom.auth.infrastructure.Events;
import io.vlingo.xoom.auth.infrastructure.ProfileData;
import io.vlingo.xoom.auth.infrastructure.UserRegistrationData;
import io.vlingo.xoom.auth.model.user.*;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.turbo.ComponentRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/projections#implementing-with-the-statestoreprojectionactor">
 *   StateStoreProjectionActor
 * </a>
 */
public class UserProjectionActor extends StateStoreProjectionActor<UserRegistrationData> {

  private static final UserRegistrationData Empty = UserRegistrationData.empty();

  public UserProjectionActor() {
    this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
  }

  public UserProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected UserRegistrationData currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected UserRegistrationData merge(final UserRegistrationData previousData, final int previousVersion, final UserRegistrationData currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    UserRegistrationData merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case UserRegistered: {
          final UserRegistered typedEvent = typed(event);
          final ProfileData profile = ProfileData.from(typedEvent.profile);
          final Set<CredentialData> credentials = typedEvent.credentials.stream().map(c -> CredentialData.from(c)).collect(Collectors.toSet());
          merged = UserRegistrationData.from(typedEvent.userId, typedEvent.username, profile, credentials, typedEvent.active);
          break;
        }

        case UserActivated: {
          final UserActivated typedEvent = typed(event);
          merged = UserRegistrationData.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, true);
          break;
        }

        case UserDeactivated: {
          final UserDeactivated typedEvent = typed(event);
          merged = UserRegistrationData.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, false);
          break;
        }

        case UserCredentialAdded: {
          final UserCredentialAdded typedEvent = typed(event);
          final CredentialData credential = CredentialData.from(typedEvent.credential);
          previousData.credentials.add(credential);
          merged = UserRegistrationData.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, previousData.active);
          break;
        }

        case UserCredentialRemoved: {
          final UserCredentialRemoved typedEvent = typed(event);
          previousData.credentials.stream().filter(credential -> credential.authority.equals(typedEvent.authority))
                  .forEach(credential -> previousData.credentials.remove(credential));
          merged = UserRegistrationData.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, previousData.active);
          break;
        }

        case UserCredentialReplaced: {
          final UserCredentialReplaced typedEvent = typed(event);
          final CredentialData credential = CredentialData.from(typedEvent.credential);
          previousData.credentials.add(credential);
          merged = UserRegistrationData.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, previousData.active);
          break;
        }

        case UserProfileReplaced: {
          final UserProfileReplaced typedEvent = typed(event);
          final ProfileData profile = ProfileData.from(typedEvent.profile);
          merged = UserRegistrationData.from(typedEvent.userId, previousData.username, profile, previousData.credentials, previousData.active);
          break;
        }

        default:
          logger().warn("Event of type " + event.typeName() + " was not matched.");
          break;
      }
    }

    return merged;
  }
}
