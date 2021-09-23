package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.*;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.model.user.*;

import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.turbo.ComponentRegistry;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/projections#implementing-with-the-statestoreprojectionactor">
 *   StateStoreProjectionActor
 * </a>
 */
public class UserProjectionActor extends StateStoreProjectionActor<UserData> {

  private static final UserData Empty = UserData.empty();

  public UserProjectionActor() {
    this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
  }

  public UserProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected UserData currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected UserData merge(final UserData previousData, final int previousVersion, final UserData currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    UserData merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case UserRegistered: {
          final UserRegistered typedEvent = typed(event);
          final PersonNameData name = PersonNameData.from(typedEvent.profile.name.given, typedEvent.profile.name.family, typedEvent.profile.name.second);
          final ProfileData profile = ProfileData.from(typedEvent.profile.emailAddress, name, typedEvent.profile.phone);
          merged = UserData.from(typedEvent.id, typedEvent.tenantId, typedEvent.username, typedEvent.active, new HashSet<>(), profile);
          break;
        }

        case UserActivated: {
          final UserActivated typedEvent = typed(event);
          merged = UserData.from(typedEvent.id, typedEvent.tenantId, typedEvent.username, previousData.active, previousData.credentials, previousData.profile);
          break;
        }

        case UserDeactivated: {
          final UserDeactivated typedEvent = typed(event);
          merged = UserData.from(typedEvent.id, typedEvent.tenantId, typedEvent.username, previousData.active, previousData.credentials, previousData.profile);
          break;
        }

        case UserCredentialAdded: {
          final UserCredentialAdded typedEvent = typed(event);
          final CredentialData credential = CredentialData.from(typedEvent.credential);
          previousData.credentials.add(credential);
          merged = UserData.from(typedEvent.id, previousData.tenantId, previousData.username, previousData.active, previousData.credentials, previousData.profile);
          break;
        }

        case UserCredentialRemoved: {
          final UserCredentialRemoved typedEvent = typed(event);
          final CredentialData credential = CredentialData.from(typedEvent.credential);
          previousData.credentials.remove(credential);
          merged = UserData.from(typedEvent.id, previousData.tenantId, previousData.username, previousData.active, previousData.credentials, previousData.profile);
          break;
        }

        case UserCredentialReplaced: {
          final UserCredentialReplaced typedEvent = typed(event);
          final CredentialData credential = CredentialData.from(typedEvent.credential);
          previousData.credentials.add(credential);
          merged = UserData.from(typedEvent.id, previousData.tenantId, previousData.username, previousData.active, previousData.credentials, previousData.profile);
          break;
        }

        case UserProfileReplaced: {
          final UserProfileReplaced typedEvent = typed(event);
          final PersonNameData name = PersonNameData.from(typedEvent.profile.name.given, typedEvent.profile.name.family, typedEvent.profile.name.second);
          final ProfileData profile = ProfileData.from(typedEvent.profile.emailAddress, name, typedEvent.profile.phone);
          merged = UserData.from(typedEvent.id, typedEvent.tenantId, typedEvent.username, previousData.active, previousData.credentials, profile);
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
