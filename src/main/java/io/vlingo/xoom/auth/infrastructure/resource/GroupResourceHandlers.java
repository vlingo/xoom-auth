package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.turbo.annotation.autodispatch.Handler.Three;
import io.vlingo.xoom.turbo.annotation.autodispatch.Handler.Two;
import io.vlingo.xoom.turbo.annotation.autodispatch.HandlerEntry;

import io.vlingo.xoom.auth.model.group.Group;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.infrastructure.persistence.GroupQueries;
import io.vlingo.xoom.auth.model.group.GroupState;
import java.util.Collection;

public class GroupResourceHandlers {

  public static final int PROVISION_GROUP = 0;
  public static final int CHANGE_DESCRIPTION = 1;
  public static final int ASSIGN_GROUP = 2;
  public static final int UNASSIGN_GROUP = 3;
  public static final int ASSIGN_USER = 4;
  public static final int UNASSIGN_USER = 5;
  public static final int GROUPS = 6;
  public static final int GROUP_OF = 7;
  public static final int ADAPT_STATE = 8;

  public static final HandlerEntry<Three<Completes<GroupState>, Stage, GroupData>> PROVISION_GROUP_HANDLER =
          HandlerEntry.of(PROVISION_GROUP, ($stage, data) -> {
              return Group.provisionGroup($stage, data.name, data.description, data.tenantId);
          });

  public static final HandlerEntry<Three<Completes<GroupState>, Group, GroupData>> CHANGE_DESCRIPTION_HANDLER =
          HandlerEntry.of(CHANGE_DESCRIPTION, (group, data) -> {
              return group.changeDescription(data.description, data.tenantId);
          });

  public static final HandlerEntry<Three<Completes<GroupState>, Group, GroupData>> ASSIGN_GROUP_HANDLER =
          HandlerEntry.of(ASSIGN_GROUP, (group, data) -> {
              return group.assignGroup(data.id, data.tenantId);
          });

  public static final HandlerEntry<Three<Completes<GroupState>, Group, GroupData>> UNASSIGN_GROUP_HANDLER =
          HandlerEntry.of(UNASSIGN_GROUP, (group, data) -> {
              return group.unassignGroup(data.id, data.tenantId);
          });

  public static final HandlerEntry<Three<Completes<GroupState>, Group, GroupData>> ASSIGN_USER_HANDLER =
          HandlerEntry.of(ASSIGN_USER, (group, data) -> {
              return group.assignUser(data.id, data.tenantId);
          });

  public static final HandlerEntry<Three<Completes<GroupState>, Group, GroupData>> UNASSIGN_USER_HANDLER =
          HandlerEntry.of(UNASSIGN_USER, (group, data) -> {
              return group.unassignUser(data.id, data.tenantId);
          });

  public static final HandlerEntry<Two<GroupData, GroupState>> ADAPT_STATE_HANDLER =
          HandlerEntry.of(ADAPT_STATE, GroupData::from);

  public static final HandlerEntry<Two<Completes<Collection<GroupData>>, GroupQueries>> QUERY_ALL_HANDLER =
          HandlerEntry.of(GROUPS, GroupQueries::groups);

  public static final HandlerEntry<Three<Completes<GroupData>, GroupQueries, String>> QUERY_BY_ID_HANDLER =
          HandlerEntry.of(GROUP_OF, ($queries, id) -> $queries.groupOf(id));

}