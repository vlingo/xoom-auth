package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.Events;
import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.auth.model.role.*;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.turbo.ComponentRegistry;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/projections#implementing-with-the-statestoreprojectionactor">
 * StateStoreProjectionActor
 * </a>
 */
public class RoleProjectionActor extends StateStoreProjectionActor<RoleView> {

  private static final RoleView Empty = RoleView.empty();

  public RoleProjectionActor() {
    this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
  }

  public RoleProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected RoleView currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected RoleView merge(final RoleView previousData, final int previousVersion, final RoleView currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    RoleView merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case RoleProvisioned: {
          final RoleProvisioned typedEvent = typed(event);
          merged = RoleView.from(typedEvent.roleId, typedEvent.name, typedEvent.description);
          break;
        }

        case RoleDescriptionChanged: {
          final RoleDescriptionChanged typedEvent = typed(event);
          merged = RoleView.from(typedEvent.roleId, previousData.name, typedEvent.description, previousData.permissions, previousData.groups, previousData.users);
          break;
        }

        case GroupAssignedToRole: {
          final GroupAssignedToRole typedEvent = typed(event);
          final Set<Relation<GroupId, RoleId>> groups = concat(previousData.groups, Relation.groupAssignedToRole(typedEvent.groupId, typedEvent.roleId));
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description, previousData.permissions, groups, previousData.users);
          break;
        }

        case GroupUnassignedFromRole: {
          final GroupUnassignedFromRole typedEvent = typed(event);
          final Set<Relation<GroupId, RoleId>> groups = filter(previousData.groups, g -> !g.equals(Relation.groupAssignedToRole(typedEvent.groupId, typedEvent.roleId)));
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description, previousData.permissions, groups, previousData.users);
          break;
        }

        case UserAssignedToRole: {
          final UserAssignedToRole typedEvent = typed(event);
          final Set<Relation<UserId, RoleId>> users = concat(previousData.users, Relation.userAssignedToRole(typedEvent.userId, typedEvent.roleId));
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description, previousData.permissions, previousData.groups, users);
          break;
        }

        case UserUnassignedFromRole: {
          final UserUnassignedFromRole typedEvent = typed(event);
          final Set<Relation<UserId, RoleId>> users = filter(previousData.users, u -> !u.equals(Relation.userAssignedToRole(typedEvent.userId, typedEvent.roleId)));
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description, previousData.permissions, previousData.groups, users);
          break;
        }

        case RolePermissionAttached: {
          final RolePermissionAttached typedEvent = typed(event);
          final Set<Relation<PermissionId, RoleId>> permissions = concat(previousData.permissions, Relation.permissionAttachedToRole(typedEvent.permissionId, typedEvent.roleId));
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description, permissions, previousData.groups, previousData.users);
          break;
        }

        case RolePermissionDetached: {
          final RolePermissionDetached typedEvent = typed(event);
          final Set<Relation<PermissionId, RoleId>> permissions = filter(previousData.permissions, p -> !p.equals(Relation.permissionAttachedToRole(typedEvent.permissionId, typedEvent.roleId)));
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description, permissions, previousData.groups, previousData.users);
          break;
        }

        default:
          logger().warn("Event of type " + event.typeName() + " was not matched.");
          break;
      }
    }

    return merged;
  }


  private <T> Set<T> filter(final Set<T> items, final Predicate<T> predicate) {
    return items.stream().filter(predicate).collect(Collectors.toSet());
  }

  private <T> Set<T> concat(final Set<T> items, final T item) {
    return Stream.concat(items.stream(), Stream.of(item)).collect(Collectors.toSet());
  }
}
