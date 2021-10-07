package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.group.*;
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
public class GroupProjectionActor extends StateStoreProjectionActor<GroupData> {

  private static final GroupData Empty = GroupData.empty();

  public GroupProjectionActor() {
    this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
  }

  public GroupProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected GroupData currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected GroupData merge(final GroupData previousData, final int previousVersion, final GroupData currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    GroupData merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case GroupProvisioned: {
          final GroupProvisioned typedEvent = typed(event);
          merged = GroupData.from(typedEvent.groupId, typedEvent.name, typedEvent.description);
          break;
        }

        case GroupDescriptionChanged: {
          final GroupDescriptionChanged typedEvent = typed(event);
          merged = GroupData.from(typedEvent.groupId, previousData.name, typedEvent.description);
          break;
        }

        case GroupAssignedToGroup: {
          final GroupAssignedToGroup typedEvent = typed(event);
          merged = GroupData.from(typedEvent.groupId, previousData.name, previousData.description);
          break;
        }

        case GroupUnassignedFromGroup: {
          final GroupUnassignedFromGroup typedEvent = typed(event);
          merged = GroupData.from(typedEvent.groupId, previousData.name, previousData.description);
          break;
        }

        case UserAssignedToGroup: {
          final UserAssignedToGroup typedEvent = typed(event);
          merged = GroupData.from(typedEvent.groupId, previousData.name, previousData.description);
          break;
        }

        case UserUnassignedFromGroup: {
          final UserUnassignedFromGroup typedEvent = typed(event);
          merged = GroupData.from(typedEvent.groupId, previousData.name, previousData.description);
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
