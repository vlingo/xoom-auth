package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.role.*;
import io.vlingo.xoom.auth.infrastructure.*;

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
public class RoleProjectionActor extends StateStoreProjectionActor<RoleData> {

  private static final RoleData Empty = RoleData.empty();

  public RoleProjectionActor() {
    this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
  }

  public RoleProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected RoleData currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected RoleData merge(final RoleData previousData, final int previousVersion, final RoleData currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    RoleData merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case RoleProvisioned: {
          final RoleProvisioned typedEvent = typed(event);
          merged = RoleData.from(typedEvent.id, typedEvent.tenantId, typedEvent.name, typedEvent.description);
          break;
        }

        case RoleDescriptionChanged: {
          final RoleDescriptionChanged typedEvent = typed(event);
          merged = RoleData.from(typedEvent.id, typedEvent.tenantId, typedEvent.name, typedEvent.description);
          break;
        }

        case GroupAssignedToRole: {
          final GroupAssignedToRole typedEvent = typed(event);
          merged = RoleData.from(typedEvent.id, typedEvent.tenantId, typedEvent.name, previousData.description);
          break;
        }

        case GroupUnassignedFromRole: {
          final GroupUnassignedFromRole typedEvent = typed(event);
          merged = RoleData.from(typedEvent.id, typedEvent.tenantId, typedEvent.name, previousData.description);
          break;
        }

        case UserAssignedToRole: {
          final UserAssignedToRole typedEvent = typed(event);
          merged = RoleData.from(typedEvent.id, typedEvent.tenantId, typedEvent.name, previousData.description);
          break;
        }

        case UserUnassignedFromRole: {
          final UserUnassignedFromRole typedEvent = typed(event);
          merged = RoleData.from(typedEvent.id, typedEvent.tenantId, typedEvent.name, previousData.description);
          break;
        }

        case RolePermissionAttached: {
          final RolePermissionAttached typedEvent = typed(event);
          merged = RoleData.from(typedEvent.id, typedEvent.tenantId, typedEvent.name, previousData.description);
          break;
        }

        case RolePermissionDetached: {
          final RolePermissionDetached typedEvent = typed(event);
          merged = RoleData.from(typedEvent.id, typedEvent.tenantId, typedEvent.name, previousData.description);
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
