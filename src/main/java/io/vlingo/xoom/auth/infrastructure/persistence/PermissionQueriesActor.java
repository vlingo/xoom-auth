package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.ArrayList;
import java.util.Collection;

import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.symbio.store.state.StateStore;

import io.vlingo.xoom.auth.infrastructure.PermissionData;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#querying-a-statestore">Querying a StateStore</a>
 */
@SuppressWarnings("all")
public class PermissionQueriesActor extends StateStoreQueryActor implements PermissionQueries {

  public PermissionQueriesActor(StateStore store) {
    super(store);
  }

  @Override
  public Completes<PermissionView> permissionOf(PermissionId permissionId) {
    return queryStateFor(permissionId.idString(), PermissionView.class, PermissionView.empty());
  }

  @Override
  public Completes<Collection<PermissionView>> permissions() {
    return streamAllOf(PermissionView.class, new ArrayList<>());
  }

}
