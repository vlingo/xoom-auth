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
import io.vlingo.xoom.auth.infrastructure.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoleQueriesTest {

  private World world;
  private StateStore stateStore;
  private RoleQueries queries;

  @BeforeEach
  public void setUp(){
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, RoleData.class);
    queries = world.actorFor(RoleQueries.class, RoleQueriesActor.class, stateStore);
  }

  private static final RoleData FIRST_QUERY_BY_ID_TEST_DATA = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
  private static final RoleData SECOND_QUERY_BY_ID_TEST_DATA = RoleData.from("2", "second-role-tenantId", "second-role-name", "second-role-description");

  @Test
  public void queryById() {
    stateStore.write("1", FIRST_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write("2", SECOND_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);

    final RoleData firstData = queries.roleOf("1").await();

    assertEquals(firstData.id, "1");
    assertEquals(firstData.tenantId, "first-role-tenantId");
    assertEquals(firstData.name, "first-role-name");
    assertEquals(firstData.description, "first-role-description");

    final RoleData secondData = queries.roleOf("2").await();

    assertEquals(secondData.id, "2");
    assertEquals(secondData.tenantId, "second-role-tenantId");
    assertEquals(secondData.name, "second-role-name");
    assertEquals(secondData.description, "second-role-description");
  }

  private static final RoleData FIRST_QUERY_ALL_TEST_DATA = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
  private static final RoleData SECOND_QUERY_ALL_TEST_DATA = RoleData.from("2", "second-role-tenantId", "second-role-name", "second-role-description");

  @Test
  public void queryAll() {
    stateStore.write("1", FIRST_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write("2", SECOND_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);

    final Collection<RoleData> results = queries.roles().await();
    final RoleData firstData = results.stream().filter(data -> data.id.equals("1")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstData.id, "1");
    assertEquals(firstData.tenantId, "first-role-tenantId");
    assertEquals(firstData.name, "first-role-name");
    assertEquals(firstData.description, "first-role-description");

    final RoleData secondData = results.stream().filter(data -> data.id.equals("2")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondData.id, "2");
    assertEquals(secondData.tenantId, "second-role-tenantId");
    assertEquals(secondData.name, "second-role-name");
    assertEquals(secondData.description, "second-role-description");
  }

  @Test
  public void roleOfEmptyResult(){
    final RoleData result = queries.roleOf("1").await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}