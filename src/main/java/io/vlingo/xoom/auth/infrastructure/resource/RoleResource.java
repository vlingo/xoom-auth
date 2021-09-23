package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.turbo.annotation.autodispatch.*;
import io.vlingo.xoom.http.Response;

import io.vlingo.xoom.auth.model.role.RoleEntity;
import io.vlingo.xoom.auth.infrastructure.RoleData;
import io.vlingo.xoom.auth.infrastructure.persistence.RoleQueriesActor;
import io.vlingo.xoom.auth.infrastructure.persistence.RoleQueries;
import io.vlingo.xoom.auth.model.role.Role;

import static io.vlingo.xoom.http.Method.*;

@AutoDispatch(path="/tenants", handlers=RoleResourceHandlers.class)
@Queries(protocol = RoleQueries.class, actor = RoleQueriesActor.class)
@Model(protocol = Role.class, actor = RoleEntity.class, data = RoleData.class)
public interface RoleResource {

  @Route(method = POST, path = "/{tenantId}/roles", handler = RoleResourceHandlers.PROVISION_ROLE)
  @ResponseAdapter(handler = RoleResourceHandlers.ADAPT_STATE)
  Completes<Response> provisionRole(@Body final RoleData data);

  @Route(method = PATCH, path = "/{tenantId}/roles/{roleName}/description", handler = RoleResourceHandlers.CHANGE_DESCRIPTION)
  @ResponseAdapter(handler = RoleResourceHandlers.ADAPT_STATE)
  Completes<Response> changeDescription(@Id final String id, @Body final RoleData data);

  @Route(method = PUT, path = "/{tenantId}/roles/{roleName}/groups", handler = RoleResourceHandlers.ASSIGN_GROUP)
  @ResponseAdapter(handler = RoleResourceHandlers.ADAPT_STATE)
  Completes<Response> assignGroup(@Id final String id, @Body final RoleData data);

  @Route(method = DELETE, path = "/{tenantId}/roles/{roleName}/groups/{groupName}", handler = RoleResourceHandlers.UNASSIGN_GROUP)
  @ResponseAdapter(handler = RoleResourceHandlers.ADAPT_STATE)
  Completes<Response> unassignGroup(@Id final String id, @Body final RoleData data);

  @Route(method = PUT, path = "/{tenantId}/roles/{roleName}/users", handler = RoleResourceHandlers.ASSIGN_USER)
  @ResponseAdapter(handler = RoleResourceHandlers.ADAPT_STATE)
  Completes<Response> assignUser(@Id final String id, @Body final RoleData data);

  @Route(method = DELETE, path = "/{tenantId}/roles/{roleName}/users/{username}", handler = RoleResourceHandlers.UNASSIGN_USER)
  @ResponseAdapter(handler = RoleResourceHandlers.ADAPT_STATE)
  Completes<Response> unassignUser(@Id final String id, @Body final RoleData data);

  @Route(method = PUT, path = "/{tenantId}/roles/{roleName}/permissions", handler = RoleResourceHandlers.ATTACH)
  @ResponseAdapter(handler = RoleResourceHandlers.ADAPT_STATE)
  Completes<Response> attach(@Id final String id, @Body final RoleData data);

  @Route(method = DELETE, path = "/{tenantId}/roles/{roleName}/permissions/{permissionName}", handler = RoleResourceHandlers.DETACH)
  @ResponseAdapter(handler = RoleResourceHandlers.ADAPT_STATE)
  Completes<Response> detach(@Id final String id, @Body final RoleData data);

  @Route(method = GET, path = "/{tenantId}/roles", handler = RoleResourceHandlers.ROLES)
  Completes<Response> roles();

  @Route(method = GET, path = "/{tenantId}/roles/{id}", handler = RoleResourceHandlers.ROLE_OF)
  Completes<Response> roleOf(final String id);

}