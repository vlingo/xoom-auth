package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.Events;
import io.vlingo.xoom.auth.model.role.*;
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
          merged = RoleView.from(typedEvent.roleId, previousData.name, typedEvent.description);
          break;
        }

        case GroupAssignedToRole: {
          final GroupAssignedToRole typedEvent = typed(event);
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description);
          break;
        }

        case GroupUnassignedFromRole: {
          final GroupUnassignedFromRole typedEvent = typed(event);
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description);
          break;
        }

        case UserAssignedToRole: {
          final UserAssignedToRole typedEvent = typed(event);
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description);
          break;
        }

        case UserUnassignedFromRole: {
          final UserUnassignedFromRole typedEvent = typed(event);
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description);
          break;
        }

        case RolePermissionAttached: {
          final RolePermissionAttached typedEvent = typed(event);
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description);
          break;
        }

        case RolePermissionDetached: {
          final RolePermissionDetached typedEvent = typed(event);
          merged = RoleView.from(typedEvent.roleId, previousData.name, previousData.description);
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
