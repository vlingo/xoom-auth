package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.Events;
import io.vlingo.xoom.auth.model.group.*;
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
          merged = GroupView.from(typedEvent.groupId, previousData.name, typedEvent.description, groupIds(previousData.groups), userIds(previousData.users));
          break;
        }

        case GroupAssignedToGroup: {
          final GroupAssignedToGroup typedEvent = typed(event);
          final Set<GroupId> innerGroups = concat(groupIds(previousData.groups), typedEvent.innerGroupId);
          merged = GroupView.from(typedEvent.groupId, previousData.name, previousData.description, innerGroups, userIds(previousData.users));
          break;
        }

        case GroupUnassignedFromGroup: {
          final GroupUnassignedFromGroup typedEvent = typed(event);
          final Set<GroupId> innerGroups = filter(groupIds(previousData.groups), g -> !g.equals(typedEvent.innerGroupId));
          merged = GroupView.from(typedEvent.groupId, previousData.name, previousData.description, innerGroups, userIds(previousData.users));
          break;
        }

        case UserAssignedToGroup: {
          final UserAssignedToGroup typedEvent = typed(event);
          final Set<UserId> users = concat(userIds(previousData.users), typedEvent.userId);
          merged = GroupView.from(typedEvent.groupId, previousData.name, previousData.description, groupIds(previousData.groups), users);
          break;
        }

        case UserUnassignedFromGroup: {
          final UserUnassignedFromGroup typedEvent = typed(event);
          final Set<UserId> users = filter(userIds(previousData.users), u -> !u.equals(typedEvent.userId));
          merged = GroupView.from(typedEvent.groupId, previousData.name, previousData.description, groupIds(previousData.groups), users);
          break;
        }

        default:
          logger().warn("Event of type " + event.typeName() + " was not matched.");
          break;
      }
    }

    return merged;
  }

  private Set<GroupId> groupIds(final Set<String> groups) {
    return groups.stream().map(g -> GroupId.from(g)).collect(Collectors.toSet());
  }

  private Set<UserId> userIds(final Set<String> users) {
    return users.stream().map(u -> UserId.from(u)).collect(Collectors.toSet());
  }

  private <T> Set<T> filter(final Set<T> items, final Predicate<T> predicate) {
    return items.stream().filter(predicate).collect(Collectors.toSet());
  }

  private <T> Set<T> concat(final Set<T> items, final T item) {
    return Stream.concat(items.stream(), Stream.of(item)).collect(Collectors.toSet());
  }
}
