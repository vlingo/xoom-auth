package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.Events;
import io.vlingo.xoom.auth.infrastructure.persistence.UserView.CredentialView;
import io.vlingo.xoom.auth.infrastructure.persistence.UserView.ProfileView;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.role.UserAssignedToRole;
import io.vlingo.xoom.auth.model.role.UserUnassignedFromRole;
import io.vlingo.xoom.auth.model.user.*;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.turbo.ComponentRegistry;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
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
          merged = UserView.from(typedEvent.userId, typedEvent.username, profile, credentials, typedEvent.active, Collections.emptySet());
          break;
        }

        case UserActivated: {
          final UserActivated typedEvent = typed(event);
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, true, previousData.roles);
          break;
        }

        case UserDeactivated: {
          final UserDeactivated typedEvent = typed(event);
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, false, previousData.roles);
          break;
        }

        case UserCredentialAdded: {
          final UserCredentialAdded typedEvent = typed(event);
          final CredentialView credential = CredentialView.from(typedEvent.credential);
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, includeCredential(previousData.credentials, credential), previousData.active, previousData.roles);
          break;
        }

        case UserCredentialRemoved: {
          final UserCredentialRemoved typedEvent = typed(event);
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, removeCredential(previousData.credentials, typedEvent.authority), previousData.active, previousData.roles);
          break;
        }

        case UserCredentialReplaced: {
          final UserCredentialReplaced typedEvent = typed(event);
          final CredentialView credential = CredentialView.from(typedEvent.credential);
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, includeCredential(removeCredential(previousData.credentials, typedEvent.authority), credential), previousData.active, previousData.roles);
          break;
        }

        case UserProfileReplaced: {
          final UserProfileReplaced typedEvent = typed(event);
          final ProfileView profile = ProfileView.from(typedEvent.profile);
          merged = UserView.from(typedEvent.userId, previousData.username, profile, previousData.credentials, previousData.active, previousData.roles);
          break;
        }

        case UserAssignedToRole: {
          final UserAssignedToRole typedEvent = typed(event);
          final Set<Relation<UserId, RoleId>> roles = concat(previousData.roles, Relation.userAssignedToRole(typedEvent.userId, typedEvent.roleId));
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, previousData.active, roles);
          break;
        }

        case UserUnassignedFromRole: {
          final UserUnassignedFromRole typedEvent = typed(event);
          final Set<Relation<UserId, RoleId>> roles = filter(previousData.roles, r -> !r.equals(Relation.userAssignedToRole(typedEvent.userId, typedEvent.roleId)));
          merged = UserView.from(typedEvent.userId, previousData.username, previousData.profile, previousData.credentials, previousData.active, roles);
          break;
        }

        default:
          logger().warn("Event of type " + event.typeName() + " was not matched.");
          break;
      }
    }

    return merged;
  }

  @Override
  protected String dataIdFor(final Projectable projectable) {
    final Source<?> firstEvent = sources().get(0);
    switch (Events.valueOf(firstEvent.typeName())) {
      case UserAssignedToRole:
        return this.<UserAssignedToRole>typed(firstEvent).userId.idString();
      case UserUnassignedFromRole:
        return this.<UserUnassignedFromRole>typed(firstEvent).userId.idString();
      default:
        return super.dataIdFor(projectable);
    }
  }

  @Override
  protected int currentDataVersionFor(final Projectable projectable, final UserView previousData, final int previousVersion) {
    switch (Events.valueOf(sources().get(0).typeName())) {
      case UserAssignedToRole:
      case UserUnassignedFromRole:
        return previousVersion + 1;
      default:
        return super.currentDataVersionFor(projectable, previousData, previousVersion);
    }
  }

  private Set<CredentialView> includeCredential(final Set<CredentialView> credentials, final CredentialView credential) {
    return concat(credentials, credential);
  }

  private Set<CredentialView> removeCredential(final Set<CredentialView> credentials, final String authority) {
    return filter(credentials, c -> !c.authority.equals(authority));
  }

  private <T> Set<T> filter(final Set<T> items, final Predicate<T> predicate) {
    return items.stream().filter(predicate).collect(Collectors.toSet());
  }

  private <T> Set<T> concat(final Set<T> items, final T item) {
    return Stream.concat(items.stream(), Stream.of(item)).collect(Collectors.toSet());
  }
}
