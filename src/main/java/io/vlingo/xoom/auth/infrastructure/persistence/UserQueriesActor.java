package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.Collection;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#querying-a-statestore">Querying a StateStore</a>
 */
@SuppressWarnings("all")
public class UserQueriesActor extends StateStoreQueryActor implements UserQueries {

  public UserQueriesActor(StateStore store) {
    super(store);
  }

  @Override
  public Completes<UserView> userOf(UserId userId) {
    return queryStateFor(userId.idString(), UserView.class, UserView.empty());
  }

  @Override
  public Completes<Collection<UserView>> users() {
    return streamAllOf(UserView.class, new ArrayList<>());
  }

}
