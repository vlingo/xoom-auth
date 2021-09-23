package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
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
import java.time.LocalDateTime;
import java.util.*;
import io.vlingo.xoom.auth.infrastructure.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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

  private static final UserRegistrationData FIRST_QUERY_BY_ID_TEST_DATA = UserRegistrationData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
  private static final UserRegistrationData SECOND_QUERY_BY_ID_TEST_DATA = UserRegistrationData.from("2", "second-user-tenantId", "second-user-username", true, new HashSet<>(), ProfileData.from("second-user-profile-emailAddress", PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-phone"));

  @Test
  public void queryById() {
    stateStore.write("1", FIRST_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write("2", SECOND_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);

    final UserRegistrationData firstData = queries.userOf("1").await();

    assertEquals(firstData.id, "1");
    assertEquals(firstData.tenantId, "first-user-tenantId");
    assertEquals(firstData.username, "first-user-username");
    assertEquals(firstData.active, true);
    assertNotNull(firstData.credentials);
    assertEquals(firstData.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(firstData.profile.name.given, "first-user-profile-name-given");
    assertEquals(firstData.profile.name.family, "first-user-profile-name-family");
    assertEquals(firstData.profile.name.second, "first-user-profile-name-second");
    assertEquals(firstData.profile.phone, "first-user-profile-phone");

    final UserRegistrationData secondData = queries.userOf("2").await();

    assertEquals(secondData.id, "2");
    assertEquals(secondData.tenantId, "second-user-tenantId");
    assertEquals(secondData.username, "second-user-username");
    assertEquals(secondData.active, true);
    assertNotNull(secondData.credentials);
    assertEquals(secondData.profile.emailAddress, "second-user-profile-emailAddress");
    assertEquals(secondData.profile.name.given, "second-user-profile-name-given");
    assertEquals(secondData.profile.name.family, "second-user-profile-name-family");
    assertEquals(secondData.profile.name.second, "second-user-profile-name-second");
    assertEquals(secondData.profile.phone, "second-user-profile-phone");
  }

  private static final UserRegistrationData FIRST_QUERY_ALL_TEST_DATA = UserRegistrationData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
  private static final UserRegistrationData SECOND_QUERY_ALL_TEST_DATA = UserRegistrationData.from("2", "second-user-tenantId", "second-user-username", true, new HashSet<>(), ProfileData.from("second-user-profile-emailAddress", PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-phone"));

  @Test
  public void queryAll() {
    stateStore.write("1", FIRST_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write("2", SECOND_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);

    final Collection<UserRegistrationData> results = queries.users().await();
    final UserRegistrationData firstData = results.stream().filter(data -> data.id.equals("1")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstData.id, "1");
    assertEquals(firstData.tenantId, "first-user-tenantId");
    assertEquals(firstData.username, "first-user-username");
    assertEquals(firstData.active, true);
    assertNotNull(firstData.credentials);
    assertEquals(firstData.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(firstData.profile.name.given, "first-user-profile-name-given");
    assertEquals(firstData.profile.name.family, "first-user-profile-name-family");
    assertEquals(firstData.profile.name.second, "first-user-profile-name-second");
    assertEquals(firstData.profile.phone, "first-user-profile-phone");

    final UserRegistrationData secondData = results.stream().filter(data -> data.id.equals("2")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondData.id, "2");
    assertEquals(secondData.tenantId, "second-user-tenantId");
    assertEquals(secondData.username, "second-user-username");
    assertEquals(secondData.active, true);
    assertNotNull(secondData.credentials);
    assertEquals(secondData.profile.emailAddress, "second-user-profile-emailAddress");
    assertEquals(secondData.profile.name.given, "second-user-profile-name-given");
    assertEquals(secondData.profile.name.family, "second-user-profile-name-family");
    assertEquals(secondData.profile.name.second, "second-user-profile-name-second");
    assertEquals(secondData.profile.phone, "second-user-profile-phone");
  }

  @Test
  public void userOfEmptyResult(){
    final UserRegistrationData result = queries.userOf("1").await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}