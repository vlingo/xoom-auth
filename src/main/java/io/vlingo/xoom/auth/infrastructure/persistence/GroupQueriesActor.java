package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.ArrayList;
import java.util.Collection;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.symbio.store.state.StateStore;

import io.vlingo.xoom.auth.infrastructure.GroupData;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#querying-a-statestore">Querying a StateStore</a>
 */
@SuppressWarnings("all")
public class GroupQueriesActor extends StateStoreQueryActor implements GroupQueries {

  public GroupQueriesActor(StateStore store) {
    super(store);
  }

  @Override
  public Completes<GroupData> groupOf(String id) {
    return queryStateFor(id, GroupData.class, GroupData.empty());
  }

  @Override
  public Completes<Collection<GroupData>> groups() {
    return streamAllOf(GroupData.class, new ArrayList<>());
  }

}
