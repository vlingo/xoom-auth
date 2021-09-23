package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.Collection;
import io.vlingo.xoom.common.Completes;

import io.vlingo.xoom.auth.infrastructure.TenantData;

@SuppressWarnings("all")
public interface TenantQueries {
  Completes<TenantData> tenantOf(String id);
  Completes<Collection<TenantData>> tenants();
}