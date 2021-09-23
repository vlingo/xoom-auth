package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.permission.*;
import java.util.*;
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
public class PermissionProjectionActor extends StateStoreProjectionActor<PermissionData> {

  private static final PermissionData Empty = PermissionData.empty();

  public PermissionProjectionActor() {
    this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
  }

  public PermissionProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected PermissionData currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected PermissionData merge(final PermissionData previousData, final int previousVersion, final PermissionData currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    PermissionData merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case PermissionProvisioned: {
          final PermissionProvisioned typedEvent = typed(event);
          merged = PermissionData.from(typedEvent.id, new HashSet<>(), typedEvent.description, typedEvent.name, typedEvent.tenantId);
          break;
        }

        case PermissionConstraintEnforced: {
          final PermissionConstraintEnforced typedEvent = typed(event);
          final ConstraintData constraint = ConstraintData.from(typedEvent.constraint);
          previousData.constraints.add(constraint);
          merged = PermissionData.from(typedEvent.id, previousData.constraints, previousData.description, previousData.name, previousData.tenantId);
          break;
        }

        case PermissionConstraintReplacementEnforced: {
          final PermissionConstraintReplacementEnforced typedEvent = typed(event);
          final ConstraintData constraint = ConstraintData.from(typedEvent.constraint);
          previousData.constraints.add(constraint);
          merged = PermissionData.from(typedEvent.id, previousData.constraints, previousData.description, previousData.name, previousData.tenantId);
          break;
        }

        case PermissionConstraintForgotten: {
          final PermissionConstraintForgotten typedEvent = typed(event);
          final ConstraintData constraint = ConstraintData.from(typedEvent.constraint);
          previousData.constraints.add(constraint);
          merged = PermissionData.from(typedEvent.id, previousData.constraints, previousData.description, previousData.name, previousData.tenantId);
          break;
        }

        case PermissionDescriptionChanged: {
          final PermissionDescriptionChanged typedEvent = typed(event);
          merged = PermissionData.from(typedEvent.id, previousData.constraints, typedEvent.description, previousData.name, typedEvent.tenantId);
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
