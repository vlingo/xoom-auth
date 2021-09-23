package io.vlingo.xoom.auth.model.group;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.actors.Stage;

public interface Group {

  Completes<GroupState> provisionGroup(final String name, final String description, final String tenantId);

  static Completes<GroupState> provisionGroup(final Stage stage, final String name, final String description, final String tenantId) {
    final io.vlingo.xoom.actors.Address _address = stage.addressFactory().uniquePrefixedWith("g-");
    final Group _group = stage.actorFor(Group.class, Definition.has(GroupEntity.class, Definition.parameters(_address.idString())), _address);
    return _group.provisionGroup(name, description, tenantId);
  }

  Completes<GroupState> changeDescription(final String description, final String tenantId);

  Completes<GroupState> assignGroup(final String id, final String tenantId);

  Completes<GroupState> unassignGroup(final String id, final String tenantId);

  Completes<GroupState> assignUser(final String id, final String tenantId);

  Completes<GroupState> unassignUser(final String id, final String tenantId);

}