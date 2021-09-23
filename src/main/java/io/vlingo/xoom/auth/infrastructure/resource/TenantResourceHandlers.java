package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.turbo.annotation.autodispatch.Handler.Three;
import io.vlingo.xoom.turbo.annotation.autodispatch.Handler.Two;
import io.vlingo.xoom.turbo.annotation.autodispatch.HandlerEntry;

import io.vlingo.xoom.auth.model.tenant.TenantState;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.infrastructure.persistence.TenantQueries;
import io.vlingo.xoom.auth.model.tenant.Tenant;
import java.util.Collection;

public class TenantResourceHandlers {

  public static final int SUBSCRIBE_FOR = 0;
  public static final int ACTIVATE = 1;
  public static final int DEACTIVATE = 2;
  public static final int CHANGE_DESCRIPTION = 3;
  public static final int CHANGE_NAME = 4;
  public static final int TENANTS = 5;
  public static final int TENANT_OF = 6;
  public static final int ADAPT_STATE = 7;

  public static final HandlerEntry<Three<Completes<TenantState>, Stage, TenantData>> SUBSCRIBE_FOR_HANDLER =
          HandlerEntry.of(SUBSCRIBE_FOR, ($stage, data) -> {
              return Tenant.subscribeFor($stage);
          });

  public static final HandlerEntry<Three<Completes<TenantState>, Tenant, TenantData>> ACTIVATE_HANDLER =
          HandlerEntry.of(ACTIVATE, (tenant, data) -> {
              return tenant.activate(data.id);
          });

  public static final HandlerEntry<Three<Completes<TenantState>, Tenant, TenantData>> DEACTIVATE_HANDLER =
          HandlerEntry.of(DEACTIVATE, (tenant, data) -> {
              return tenant.deactivate(data.id);
          });

  public static final HandlerEntry<Three<Completes<TenantState>, Tenant, TenantData>> CHANGE_DESCRIPTION_HANDLER =
          HandlerEntry.of(CHANGE_DESCRIPTION, (tenant, data) -> {
              return tenant.changeDescription(data.id, data.description);
          });

  public static final HandlerEntry<Three<Completes<TenantState>, Tenant, TenantData>> CHANGE_NAME_HANDLER =
          HandlerEntry.of(CHANGE_NAME, (tenant, data) -> {
              return tenant.changeName(data.id, data.name);
          });

  public static final HandlerEntry<Two<TenantData, TenantState>> ADAPT_STATE_HANDLER =
          HandlerEntry.of(ADAPT_STATE, TenantData::from);

  public static final HandlerEntry<Two<Completes<Collection<TenantData>>, TenantQueries>> QUERY_ALL_HANDLER =
          HandlerEntry.of(TENANTS, TenantQueries::tenants);

  public static final HandlerEntry<Three<Completes<TenantData>, TenantQueries, String>> QUERY_BY_ID_HANDLER =
          HandlerEntry.of(TENANT_OF, ($queries, id) -> $queries.tenantOf(id));

}