package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.ArrayList;
import java.util.Collection;

import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.symbio.store.state.StateStore;

import io.vlingo.xoom.auth.infrastructure.RoleData;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#querying-a-statestore">Querying a StateStore</a>
 */
@SuppressWarnings("all")
public class RoleQueriesActor extends StateStoreQueryActor implements RoleQueries {

  public RoleQueriesActor(StateStore store) {
    super(store);
  }

  @Override
  public Completes<RoleData> roleOf(RoleId roleId) {
    return queryStateFor(roleId.idString(), RoleData.class, RoleData.empty());
  }

  @Override
  public Completes<Collection<RoleData>> roles() {
    return streamAllOf(RoleData.class, new ArrayList<>());
  }

}
