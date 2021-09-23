package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.ArrayList;
import java.util.Collection;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.symbio.store.state.StateStore;

import io.vlingo.xoom.auth.infrastructure.TenantData;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#querying-a-statestore">Querying a StateStore</a>
 */
@SuppressWarnings("all")
public class TenantQueriesActor extends StateStoreQueryActor implements TenantQueries {

  public TenantQueriesActor(StateStore store) {
    super(store);
  }

  @Override
  public Completes<TenantData> tenantOf(String id) {
    return queryStateFor(id, TenantData.class, TenantData.empty());
  }

  @Override
  public Completes<Collection<TenantData>> tenants() {
    return streamAllOf(TenantData.class, new ArrayList<>());
  }

}
