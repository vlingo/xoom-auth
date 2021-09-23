package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.turbo.annotation.autodispatch.*;
import io.vlingo.xoom.http.Response;

import io.vlingo.xoom.auth.model.group.GroupEntity;
import io.vlingo.xoom.auth.infrastructure.GroupData;
import io.vlingo.xoom.auth.model.group.Group;
import io.vlingo.xoom.auth.infrastructure.persistence.GroupQueries;
import io.vlingo.xoom.auth.infrastructure.persistence.GroupQueriesActor;

import static io.vlingo.xoom.http.Method.*;

@AutoDispatch(path="/tenants", handlers=GroupResourceHandlers.class)
@Queries(protocol = GroupQueries.class, actor = GroupQueriesActor.class)
@Model(protocol = Group.class, actor = GroupEntity.class, data = GroupData.class)
public interface GroupResource {

  @Route(method = POST, path = "/{tenantId}/groups", handler = GroupResourceHandlers.PROVISION_GROUP)
  @ResponseAdapter(handler = GroupResourceHandlers.ADAPT_STATE)
  Completes<Response> provisionGroup(@Body final GroupData data);

  @Route(method = PATCH, path = "/{tenantId}/groups/{groupName}/description", handler = GroupResourceHandlers.CHANGE_DESCRIPTION)
  @ResponseAdapter(handler = GroupResourceHandlers.ADAPT_STATE)
  Completes<Response> changeDescription(@Id final String id, @Body final GroupData data);

  @Route(method = PUT, path = "/{tenantId}/groups/{groupName}/groups/{innerGroupName}", handler = GroupResourceHandlers.ASSIGN_GROUP)
  @ResponseAdapter(handler = GroupResourceHandlers.ADAPT_STATE)
  Completes<Response> assignGroup(@Id final String id, @Body final GroupData data);

  @Route(method = DELETE, path = "/{tenantId}/groups/{groupName}/groups/{innerGroupName}", handler = GroupResourceHandlers.UNASSIGN_GROUP)
  @ResponseAdapter(handler = GroupResourceHandlers.ADAPT_STATE)
  Completes<Response> unassignGroup(@Id final String id, @Body final GroupData data);

  @Route(method = PUT, path = "/{tenantId}/groups/{groupName}/users/{username}", handler = GroupResourceHandlers.ASSIGN_USER)
  @ResponseAdapter(handler = GroupResourceHandlers.ADAPT_STATE)
  Completes<Response> assignUser(@Id final String id, @Body final GroupData data);

  @Route(method = DELETE, path = "/{tenantId}/groups/{groupName}/users/{username}", handler = GroupResourceHandlers.UNASSIGN_USER)
  @ResponseAdapter(handler = GroupResourceHandlers.ADAPT_STATE)
  Completes<Response> unassignUser(@Id final String id, @Body final GroupData data);

  @Route(method = GET, handler = GroupResourceHandlers.GROUPS)
  Completes<Response> groups();

  @Route(method = GET, path = "/{id}", handler = GroupResourceHandlers.GROUP_OF)
  Completes<Response> groupOf(final String id);

}