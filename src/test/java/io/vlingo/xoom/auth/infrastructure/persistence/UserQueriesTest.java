package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.infrastructure.PersonNameData;
import io.vlingo.xoom.auth.infrastructure.ProfileData;
import io.vlingo.xoom.auth.infrastructure.UserRegistrationData;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static io.vlingo.xoom.auth.test.Assertions.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserQueriesTest {

  private World world;
  private StateStore stateStore;
  private UserQueries queries;

  @BeforeEach
  public void setUp() {
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, UserRegistrationData.class);
    queries = world.actorFor(UserQueries.class, UserQueriesActor.class, stateStore);
  }

  @AfterEach
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void itQueriesTheUserById() {
    final UserId firstUserId = UserId.from(TenantId.from("first-user-tenantId"), "first-user-username");
    final UserRegistrationData firstUser = UserRegistrationData.from(firstUserId, "first-user-username", ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"), new HashSet<>(), true);
    final UserId secondUserId = UserId.from(TenantId.from("second-user-tenantId"), "second-user-username");
    final UserRegistrationData secondUser = UserRegistrationData.from(secondUserId, "second-user-username", ProfileData.from("second-user-profile-emailAddress", PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-phone"), new HashSet<>(), true);

    givenUsersExist(firstUser, secondUser);

    assertCompletes(queries.userOf(firstUserId), user -> assertEquals(firstUser, user));
    assertCompletes(queries.userOf(secondUserId), user -> assertEquals(secondUser, user));
  }

  @Test
  public void itQueriesAllUsers() {
    final UserId firstUserId = UserId.from(TenantId.from("first-user-tenantId"), "first-user-username");
    final UserRegistrationData firstUser = UserRegistrationData.from(firstUserId, "first-user-username", ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"), new HashSet<>(), true);
    final UserId secondUserId = UserId.from(TenantId.from("second-user-tenantId"), "second-user-username");
    final UserRegistrationData secondUser = UserRegistrationData.from(secondUserId, "second-user-username", ProfileData.from("second-user-profile-emailAddress", PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-phone"), new HashSet<>(), true);

    givenUsersExist(firstUser, secondUser);

    final Completes<Collection<UserRegistrationData>> outcome = queries.users();

    assertCompletes(outcome, users -> {
      assertContains(firstUser, users);
      assertContains(secondUser, users);
    });
  }

  @Test
  public void itReturnsAnEmptyUserIfItIsNotFound() {
    final Completes<UserRegistrationData> result = queries.userOf(UserId.from(TenantId.from("68b0a384-52b4-453a-8893-daf8fcb508f6"), "bob"));

    assertCompletes(result, user -> assertEquals("", user.id));
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {
    }
  };

  private void givenUsersExist(final UserRegistrationData... users) {
    Arrays.stream(users).forEach(user -> stateStore.write(user.id, user, 1, NOOP_WRITER));
  }
}