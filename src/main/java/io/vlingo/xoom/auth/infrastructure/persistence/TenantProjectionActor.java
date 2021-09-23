package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.tenant.*;
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
public class TenantProjectionActor extends StateStoreProjectionActor<TenantData> {

  private static final TenantData Empty = TenantData.empty();

  public TenantProjectionActor() {
    this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
  }

  public TenantProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected TenantData currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected TenantData merge(final TenantData previousData, final int previousVersion, final TenantData currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    TenantData merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case TenantSubscribed: {
          final TenantSubscribed typedEvent = typed(event);
          merged = TenantData.from(typedEvent.id, typedEvent.name, typedEvent.description, typedEvent.active);
          break;
        }

        case TenantActivated: {
          final TenantActivated typedEvent = typed(event);
          merged = TenantData.from(typedEvent.id, previousData.name, previousData.description, true);
          break;
        }

        case TenantDeactivated: {
          final TenantDeactivated typedEvent = typed(event);
          merged = TenantData.from(typedEvent.id, previousData.name, previousData.description, false);
          break;
        }

        case TenantNameChanged: {
          final TenantNameChanged typedEvent = typed(event);
          merged = TenantData.from(typedEvent.id, typedEvent.name, previousData.description, previousData.active);
          break;
        }

        case TenantDescriptionChanged: {
          final TenantDescriptionChanged typedEvent = typed(event);
          merged = TenantData.from(typedEvent.id, previousData.name, typedEvent.description, previousData.active);
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
