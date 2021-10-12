package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.infrastructure.PersonNameData;
import io.vlingo.xoom.auth.infrastructure.ProfileData;
import io.vlingo.xoom.auth.infrastructure.UserRegistrationData;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserQueriesTest {

  private World world;
  private StateStore stateStore;
  private UserQueries queries;

  @BeforeEach
  public void setUp(){
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, UserRegistrationData.class);
    queries = world.actorFor(UserQueries.class, UserQueriesActor.class, stateStore);
  }

  @Test
  public void queryById() {
    final UserId firstUserId = UserId.from("first-user-tenantId", "first-user-username");
    final UserRegistrationData firstUser = UserRegistrationData.from(firstUserId, "first-user-username", ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"), new HashSet<>(), true);
    final UserId secondUserId = UserId.from("second-user-tenantId", "second-user-username");
    final UserRegistrationData secondUser = UserRegistrationData.from(secondUserId, "second-user-username", ProfileData.from("second-user-profile-emailAddress", PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-phone"), new HashSet<>(), true);

    stateStore.write(firstUserId.idString(), firstUser, 1, NOOP_WRITER);
    stateStore.write(secondUserId.idString(), secondUser, 1, NOOP_WRITER);

    final UserRegistrationData firstData = queries.userOf(firstUserId).await();

    assertEquals(firstUser, firstData);

    final UserRegistrationData secondData = queries.userOf(secondUserId).await();

    assertEquals(secondUser, secondData);
  }

  @Test
  public void queryAll() {
    final UserId firstUserId = UserId.from("first-user-tenantId", "first-user-username");
    final UserRegistrationData firstUser = UserRegistrationData.from(firstUserId, "first-user-username", ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"), new HashSet<>(), true);
    final UserId secondUserId = UserId.from("second-user-tenantId", "second-user-username");
    final UserRegistrationData secondUser = UserRegistrationData.from(secondUserId, "second-user-username", ProfileData.from("second-user-profile-emailAddress", PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-phone"), new HashSet<>(), true);

    stateStore.write(firstUserId.idString(), firstUser, 1, NOOP_WRITER);
    stateStore.write(secondUserId.idString(), secondUser, 1, NOOP_WRITER);

    final Collection<UserRegistrationData> results = queries.users().await();
    final UserRegistrationData firstData = results.stream().filter(data -> data.id.equals(firstUser.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstUser, firstData);

    final UserRegistrationData secondData = results.stream().filter(data -> data.id.equals(secondUser.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondUser, secondData);
  }

  @Test
  public void userOfEmptyResult(){
    final UserRegistrationData result = queries.userOf(UserId.from("1", "bob")).await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}