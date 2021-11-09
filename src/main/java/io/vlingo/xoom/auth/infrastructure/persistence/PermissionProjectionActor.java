package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.Events;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionView.ConstraintView;
import io.vlingo.xoom.auth.model.permission.*;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.role.RolePermissionAttached;
import io.vlingo.xoom.auth.model.role.RolePermissionDetached;
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
          merged = PermissionView.from(typedEvent.permissionId, new HashSet<>(), typedEvent.name, typedEvent.description, new HashSet<>());
          break;
        }

        case PermissionConstraintEnforced: {
          final PermissionConstraintEnforced typedEvent = typed(event);
          final ConstraintView constraint = ConstraintView.from(typedEvent.constraint);
          merged = PermissionView.from(typedEvent.permissionId, concat(previousData.constraints, constraint), previousData.name, previousData.description, previousData.roles);
          break;
        }

        case PermissionConstraintReplacementEnforced: {
          final PermissionConstraintReplacementEnforced typedEvent = typed(event);
          final ConstraintView constraint = ConstraintView.from(typedEvent.constraint);
          final Set<ConstraintView> constraints = concat(filter(previousData.constraints, c -> !c.name.equals(typedEvent.constraintName)), constraint);
          merged = PermissionView.from(typedEvent.permissionId, constraints, previousData.name, previousData.description, previousData.roles);
          break;
        }

        case PermissionConstraintForgotten: {
          final PermissionConstraintForgotten typedEvent = typed(event);
          final Set<ConstraintView> constraints = filter(previousData.constraints, c -> !c.name.equals(typedEvent.constraintName));
          merged = PermissionView.from(typedEvent.permissionId, constraints, previousData.name, previousData.description, previousData.roles);
          break;
        }

        case PermissionDescriptionChanged: {
          final PermissionDescriptionChanged typedEvent = typed(event);
          merged = PermissionView.from(typedEvent.permissionId, previousData.constraints, previousData.name, typedEvent.description, previousData.roles);
          break;
        }

        case RolePermissionAttached: {
          final RolePermissionAttached typedEvent = typed(event);
          final Set<Relation<RoleId, PermissionId>> roles = concat(previousData.roles, Relation.roleWithPermission(typedEvent.roleId, typedEvent.permissionId));
          merged = PermissionView.from(typedEvent.permissionId, previousData.constraints, previousData.name, previousData.description, roles);
          break;
        }

        case RolePermissionDetached: {
          final RolePermissionDetached typedEvent = typed(event);
          final Set<Relation<RoleId, PermissionId>> roles = filter(previousData.roles, r -> !r.equals(Relation.roleWithPermission(typedEvent.roleId, typedEvent.permissionId)));
          merged = PermissionView.from(typedEvent.permissionId, previousData.constraints, previousData.name, previousData.description, roles);
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
      case RolePermissionAttached:
        return this.<RolePermissionAttached>typed(firstEvent).permissionId.idString();
      case RolePermissionDetached:
        return this.<RolePermissionDetached>typed(firstEvent).permissionId.idString();
      default:
        return super.dataIdFor(projectable);
    }
  }

  @Override
  protected int currentDataVersionFor(Projectable projectable, PermissionView previousData, int previousVersion) {
    final Source<?> firstEvent = sources().get(0);
    switch (Events.valueOf(firstEvent.typeName())) {
      case RolePermissionAttached:
      case RolePermissionDetached:
        return previousVersion + 1;
      default:
        return super.currentDataVersionFor(projectable, previousData, previousVersion);
    }
  }

  private <T> Set<T> filter(final Set<T> items, final Predicate<T> predicate) {
    return items.stream().filter(predicate).collect(Collectors.toSet());
  }

  private <T> Set<T> concat(final Set<T> items, final T item) {
    return Stream.concat(items.stream(), Stream.of(item)).collect(Collectors.toSet());
  }
}
