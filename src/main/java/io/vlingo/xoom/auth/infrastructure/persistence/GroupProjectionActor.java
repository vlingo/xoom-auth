package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.Events;
import io.vlingo.xoom.auth.model.group.*;
import io.vlingo.xoom.auth.model.role.GroupAssignedToRole;
import io.vlingo.xoom.auth.model.role.GroupUnassignedFromRole;
import io.vlingo.xoom.auth.model.role.RoleId;
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
 *   StateStoreProjectionActor
 * </a>
 */
public class GroupProjectionActor extends StateStoreProjectionActor<GroupView> {

  private static final GroupView Empty = GroupView.empty();

  public GroupProjectionActor() {
    this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
  }

  public GroupProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected GroupView currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected GroupView merge(final GroupView previousData, final int previousVersion, final GroupView currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    GroupView merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case GroupProvisioned: {
          final GroupProvisioned typedEvent = typed(event);
          merged = GroupView.from(typedEvent.groupId, typedEvent.name, typedEvent.description);
          break;
        }

        case GroupDescriptionChanged: {
          final GroupDescriptionChanged typedEvent = typed(event);
          merged = GroupView.from(typedEvent.groupId, previousData.name, typedEvent.description, previousData.groups, previousData.users, previousData.roles);
          break;
        }

        case GroupAssignedToGroup: {
          final GroupAssignedToGroup typedEvent = typed(event);
          final Set<Relation<GroupId, GroupId>> innerGroups = concat(previousData.groups, Relation.groupWithMember(typedEvent.groupId, typedEvent.innerGroupId));
          merged = GroupView.from(typedEvent.groupId, previousData.name, previousData.description, innerGroups, previousData.users, previousData.roles);
          break;
        }

        case GroupUnassignedFromGroup: {
          final GroupUnassignedFromGroup typedEvent = typed(event);
          final Set<Relation<GroupId, GroupId>> innerGroups = filter(previousData.groups, g -> !g.equals(Relation.groupWithMember(typedEvent.groupId, typedEvent.innerGroupId)));
          merged = GroupView.from(typedEvent.groupId, previousData.name, previousData.description, innerGroups, previousData.users, previousData.roles);
          break;
        }

        case UserAssignedToGroup: {
          final UserAssignedToGroup typedEvent = typed(event);
          final Set<Relation<UserId, GroupId>> users = concat(previousData.users, Relation.userAssignedToGroup(typedEvent.userId, typedEvent.groupId));
          merged = GroupView.from(typedEvent.groupId, previousData.name, previousData.description, previousData.groups, users, previousData.roles);
          break;
        }

        case UserUnassignedFromGroup: {
          final UserUnassignedFromGroup typedEvent = typed(event);
          final Set<Relation<UserId, GroupId>> users = filter(previousData.users, u -> !u.equals(Relation.userAssignedToGroup(typedEvent.userId, typedEvent.groupId)));
          merged = GroupView.from(typedEvent.groupId, previousData.name, previousData.description, previousData.groups, users, previousData.roles);
          break;
        }

        case GroupAssignedToRole: {
          final GroupAssignedToRole typedEvent = typed(event);
          final Set<Relation<GroupId, RoleId>> roles = concat(previousData.roles, Relation.groupAssignedToRole(typedEvent.groupId, typedEvent.roleId));
          merged = GroupView.from(typedEvent.groupId, previousData.name, previousData.description, previousData.groups, previousData.users, roles);
          break;
        }

        case GroupUnassignedFromRole: {
          final GroupUnassignedFromRole typedEvent = typed(event);
          final Set<Relation<GroupId, RoleId>> roles = filter(previousData.roles, r -> !r.equals(Relation.groupAssignedToRole(typedEvent.groupId, typedEvent.roleId)));
          merged = GroupView.from(typedEvent.groupId, previousData.name, previousData.description, previousData.groups, previousData.users, roles);
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
      case GroupAssignedToRole:
        return this.<GroupAssignedToRole>typed(firstEvent).groupId.idString();
      case GroupUnassignedFromRole:
        return this.<GroupUnassignedFromRole>typed(firstEvent).groupId.idString();
      default:
        return super.dataIdFor(projectable);
    }
  }

  @Override
  protected int currentDataVersionFor(final Projectable projectable, final GroupView previousData, final int previousVersion) {
    switch (Events.valueOf(sources().get(0).typeName())) {
      case GroupAssignedToRole:
      case GroupUnassignedFromRole:
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
