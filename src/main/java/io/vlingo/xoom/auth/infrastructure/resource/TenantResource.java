package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.turbo.annotation.autodispatch.*;
import io.vlingo.xoom.http.Response;

import io.vlingo.xoom.auth.infrastructure.persistence.TenantQueriesActor;
import io.vlingo.xoom.auth.infrastructure.TenantData;
import io.vlingo.xoom.auth.model.tenant.TenantEntity;
import io.vlingo.xoom.auth.infrastructure.persistence.TenantQueries;
import io.vlingo.xoom.auth.model.tenant.Tenant;

import static io.vlingo.xoom.http.Method.*;

@AutoDispatch(path="/tenants", handlers=TenantResourceHandlers.class)
@Queries(protocol = TenantQueries.class, actor = TenantQueriesActor.class)
@Model(protocol = Tenant.class, actor = TenantEntity.class, data = TenantData.class)
public interface TenantResource {

  @Route(method = POST, handler = TenantResourceHandlers.SUBSCRIBE_FOR)
  @ResponseAdapter(handler = TenantResourceHandlers.ADAPT_STATE)
  Completes<Response> subscribeFor(@Body final TenantData data);

  @Route(method = PATCH, path = "/{id}/activate", handler = TenantResourceHandlers.ACTIVATE)
  @ResponseAdapter(handler = TenantResourceHandlers.ADAPT_STATE)
  Completes<Response> activate(@Id final String id, @Body final TenantData data);

  @Route(method = PATCH, path = "/{id}/deactivate", handler = TenantResourceHandlers.DEACTIVATE)
  @ResponseAdapter(handler = TenantResourceHandlers.ADAPT_STATE)
  Completes<Response> deactivate(@Id final String id, @Body final TenantData data);

  @Route(method = PATCH, path = "/{id}/description", handler = TenantResourceHandlers.CHANGE_DESCRIPTION)
  @ResponseAdapter(handler = TenantResourceHandlers.ADAPT_STATE)
  Completes<Response> changeDescription(@Id final String id, @Body final TenantData data);

  @Route(method = PATCH, path = "/{id}/name", handler = TenantResourceHandlers.CHANGE_NAME)
  @ResponseAdapter(handler = TenantResourceHandlers.ADAPT_STATE)
  Completes<Response> changeName(@Id final String id, @Body final TenantData data);

  @Route(method = GET, handler = TenantResourceHandlers.TENANTS)
  Completes<Response> tenants();

  @Route(method = GET, path = "/{id}", handler = TenantResourceHandlers.TENANT_OF)
  Completes<Response> tenantOf(final String id);

}