package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.Events;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionView.ConstraintView;
import io.vlingo.xoom.auth.model.permission.*;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.turbo.ComponentRegistry;

import java.util.HashSet;
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
public class PermissionProjectionActor extends StateStoreProjectionActor<PermissionView> {

  private static final PermissionView Empty = PermissionView.empty();

  public PermissionProjectionActor() {
    this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
  }

  public PermissionProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected PermissionView currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected PermissionView merge(final PermissionView previousData, final int previousVersion, final PermissionView currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    PermissionView merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case PermissionProvisioned: {
          final PermissionProvisioned typedEvent = typed(event);
          merged = PermissionView.from(typedEvent.permissionId, new HashSet<>(), typedEvent.name, typedEvent.description);
          break;
        }

        case PermissionConstraintEnforced: {
          final PermissionConstraintEnforced typedEvent = typed(event);
          final ConstraintView constraint = ConstraintView.from(typedEvent.constraint);
          merged = PermissionView.from(typedEvent.permissionId, concat(previousData.constraints, constraint), previousData.name, previousData.description);
          break;
        }

        case PermissionConstraintReplacementEnforced: {
          final PermissionConstraintReplacementEnforced typedEvent = typed(event);
          final ConstraintView constraint = ConstraintView.from(typedEvent.constraint);
          final Set<ConstraintView> constraints = concat(filter(previousData.constraints, c -> !c.name.equals(typedEvent.constraintName)), constraint);
          merged = PermissionView.from(typedEvent.permissionId, constraints, previousData.name, previousData.description);
          break;
        }

        case PermissionConstraintForgotten: {
          final PermissionConstraintForgotten typedEvent = typed(event);
          final Set<ConstraintView> constraints = filter(previousData.constraints, c -> !c.name.equals(typedEvent.constraintName));
          merged = PermissionView.from(typedEvent.permissionId, constraints, previousData.name, previousData.description);
          break;
        }

        case PermissionDescriptionChanged: {
          final PermissionDescriptionChanged typedEvent = typed(event);
          merged = PermissionView.from(typedEvent.permissionId, previousData.constraints, previousData.name, typedEvent.description);
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
