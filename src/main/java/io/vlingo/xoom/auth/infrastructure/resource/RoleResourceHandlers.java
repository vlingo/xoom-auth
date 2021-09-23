package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.turbo.annotation.autodispatch.Handler.Three;
import io.vlingo.xoom.turbo.annotation.autodispatch.Handler.Two;
import io.vlingo.xoom.turbo.annotation.autodispatch.HandlerEntry;

import io.vlingo.xoom.auth.model.role.RoleState;
import io.vlingo.xoom.auth.infrastructure.persistence.RoleQueries;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.model.role.Role;
import java.util.Collection;

public class RoleResourceHandlers {

  public static final int PROVISION_ROLE = 0;
  public static final int CHANGE_DESCRIPTION = 1;
  public static final int ASSIGN_GROUP = 2;
  public static final int UNASSIGN_GROUP = 3;
  public static final int ASSIGN_USER = 4;
  public static final int UNASSIGN_USER = 5;
  public static final int ATTACH = 6;
  public static final int DETACH = 7;
  public static final int ROLES = 8;
  public static final int ROLE_OF = 9;
  public static final int ADAPT_STATE = 10;

  public static final HandlerEntry<Three<Completes<RoleState>, Stage, RoleData>> PROVISION_ROLE_HANDLER =
          HandlerEntry.of(PROVISION_ROLE, ($stage, data) -> {
              return Role.provisionRole($stage, data.tenantId, data.name, data.description);
          });

  public static final HandlerEntry<Three<Completes<RoleState>, Role, RoleData>> CHANGE_DESCRIPTION_HANDLER =
          HandlerEntry.of(CHANGE_DESCRIPTION, (role, data) -> {
              return role.changeDescription(data.tenantId, data.name, data.description);
          });

  public static final HandlerEntry<Three<Completes<RoleState>, Role, RoleData>> ASSIGN_GROUP_HANDLER =
          HandlerEntry.of(ASSIGN_GROUP, (role, data) -> {
              return role.assignGroup(data.tenantId, data.name);
          });

  public static final HandlerEntry<Three<Completes<RoleState>, Role, RoleData>> UNASSIGN_GROUP_HANDLER =
          HandlerEntry.of(UNASSIGN_GROUP, (role, data) -> {
              return role.unassignGroup(data.tenantId, data.name);
          });

  public static final HandlerEntry<Three<Completes<RoleState>, Role, RoleData>> ASSIGN_USER_HANDLER =
          HandlerEntry.of(ASSIGN_USER, (role, data) -> {
              return role.assignUser(data.tenantId, data.name);
          });

  public static final HandlerEntry<Three<Completes<RoleState>, Role, RoleData>> UNASSIGN_USER_HANDLER =
          HandlerEntry.of(UNASSIGN_USER, (role, data) -> {
              return role.unassignUser(data.tenantId, data.name);
          });

  public static final HandlerEntry<Three<Completes<RoleState>, Role, RoleData>> ATTACH_HANDLER =
          HandlerEntry.of(ATTACH, (role, data) -> {
              return role.attach(data.tenantId, data.name);
          });

  public static final HandlerEntry<Three<Completes<RoleState>, Role, RoleData>> DETACH_HANDLER =
          HandlerEntry.of(DETACH, (role, data) -> {
              return role.detach(data.tenantId, data.name);
          });

  public static final HandlerEntry<Two<RoleData, RoleState>> ADAPT_STATE_HANDLER =
          HandlerEntry.of(ADAPT_STATE, RoleData::from);

  public static final HandlerEntry<Two<Completes<Collection<RoleData>>, RoleQueries>> QUERY_ALL_HANDLER =
          HandlerEntry.of(ROLES, RoleQueries::roles);

  public static final HandlerEntry<Three<Completes<RoleData>, RoleQueries, String>> QUERY_BY_ID_HANDLER =
          HandlerEntry.of(ROLE_OF, ($queries, id) -> $queries.roleOf(id));

}