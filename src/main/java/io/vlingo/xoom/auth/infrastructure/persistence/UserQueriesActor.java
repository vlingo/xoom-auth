package io.vlingo.xoom.auth.infrastructure.persistence;

import java.util.ArrayList;
import java.util.Collection;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.symbio.store.state.StateStore;

import io.vlingo.xoom.auth.infrastructure.UserData;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#querying-a-statestore">Querying a StateStore</a>
 */
@SuppressWarnings("all")
public class UserQueriesActor extends StateStoreQueryActor implements UserQueries {

  public UserQueriesActor(StateStore store) {
    super(store);
  }

  @Override
  public Completes<UserData> userOf(String id) {
    return queryStateFor(id, UserData.class, UserData.empty());
  }

  @Override
  public Completes<Collection<UserData>> users() {
    return streamAllOf(UserData.class, new ArrayList<>());
  }

}
