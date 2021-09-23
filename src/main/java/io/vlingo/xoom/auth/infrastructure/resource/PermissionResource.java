package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.turbo.annotation.autodispatch.*;
import io.vlingo.xoom.http.Response;

import io.vlingo.xoom.auth.infrastructure.PermissionData;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionQueriesActor;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionQueries;
import io.vlingo.xoom.auth.model.permission.Permission;
import io.vlingo.xoom.auth.model.permission.PermissionEntity;

import static io.vlingo.xoom.http.Method.*;

@AutoDispatch(path="/tenants", handlers=PermissionResourceHandlers.class)
@Queries(protocol = PermissionQueries.class, actor = PermissionQueriesActor.class)
@Model(protocol = Permission.class, actor = PermissionEntity.class, data = PermissionData.class)
public interface PermissionResource {

  @Route(method = POST, path = "/{tenantId}/permissions", handler = PermissionResourceHandlers.PROVISION_PERMISSION)
  @ResponseAdapter(handler = PermissionResourceHandlers.ADAPT_STATE)
  Completes<Response> provisionPermission(@Body final PermissionData data);

  @Route(method = PATCH, path = "/{tenantId}/permissions/{permissionName}/constraints", handler = PermissionResourceHandlers.ENFORCE)
  @ResponseAdapter(handler = PermissionResourceHandlers.ADAPT_STATE)
  Completes<Response> enforce(@Id final String id, @Body final PermissionData data);

  @Route(method = PATCH, path = "/{tenantId}/permissions/{permissionName}/constraints/{constraintName}", handler = PermissionResourceHandlers.ENFORCE_REPLACEMENT)
  @ResponseAdapter(handler = PermissionResourceHandlers.ADAPT_STATE)
  Completes<Response> enforceReplacement(@Id final String id, @Body final PermissionData data);

  @Route(method = DELETE, path = "/{tenantId}/permissions/{permissionName}/constraints/{constraintName}", handler = PermissionResourceHandlers.FORGET)
  @ResponseAdapter(handler = PermissionResourceHandlers.ADAPT_STATE)
  Completes<Response> forget(@Id final String id, @Body final PermissionData data);

  @Route(method = PATCH, path = "/{tenantId}/permissions/{permissionName}/description", handler = PermissionResourceHandlers.CHANGE_DESCRIPTION)
  @ResponseAdapter(handler = PermissionResourceHandlers.ADAPT_STATE)
  Completes<Response> changeDescription(@Id final String id, @Body final PermissionData data);

  @Route(method = GET, path = "/{tenantId}/permissions", handler = PermissionResourceHandlers.PERMISSIONS)
  Completes<Response> permissions();

  @Route(method = GET, path = "/{tenantId}/permissions/{id}", handler = PermissionResourceHandlers.PERMISSION_OF)
  Completes<Response> permissionOf(final String id);

}