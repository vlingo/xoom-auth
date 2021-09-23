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
import io.vlingo.xoom.auth.infrastructure.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GroupQueriesTest {

  private World world;
  private StateStore stateStore;
  private GroupQueries queries;

  @BeforeEach
  public void setUp(){
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, GroupData.class);
    queries = world.actorFor(GroupQueries.class, GroupQueriesActor.class, stateStore);
  }

  private static final GroupData FIRST_QUERY_BY_ID_TEST_DATA = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
  private static final GroupData SECOND_QUERY_BY_ID_TEST_DATA = GroupData.from("2", "second-group-name", "second-group-description", "second-group-tenantId");

  @Test
  public void queryById() {
    stateStore.write("1", FIRST_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write("2", SECOND_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);

    final GroupData firstData = queries.groupOf("1").await();

    assertEquals(firstData.id, "1");
    assertEquals(firstData.name, "first-group-name");
    assertEquals(firstData.description, "first-group-description");
    assertEquals(firstData.tenantId, "first-group-tenantId");

    final GroupData secondData = queries.groupOf("2").await();

    assertEquals(secondData.id, "2");
    assertEquals(secondData.name, "second-group-name");
    assertEquals(secondData.description, "second-group-description");
    assertEquals(secondData.tenantId, "second-group-tenantId");
  }

  private static final GroupData FIRST_QUERY_ALL_TEST_DATA = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
  private static final GroupData SECOND_QUERY_ALL_TEST_DATA = GroupData.from("2", "second-group-name", "second-group-description", "second-group-tenantId");

  @Test
  public void queryAll() {
    stateStore.write("1", FIRST_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write("2", SECOND_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);

    final Collection<GroupData> results = queries.groups().await();
    final GroupData firstData = results.stream().filter(data -> data.id.equals("1")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstData.id, "1");
    assertEquals(firstData.name, "first-group-name");
    assertEquals(firstData.description, "first-group-description");
    assertEquals(firstData.tenantId, "first-group-tenantId");

    final GroupData secondData = results.stream().filter(data -> data.id.equals("2")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondData.id, "2");
    assertEquals(secondData.name, "second-group-name");
    assertEquals(secondData.description, "second-group-description");
    assertEquals(secondData.tenantId, "second-group-tenantId");
  }

  @Test
  public void groupOfEmptyResult(){
    final GroupData result = queries.groupOf("1").await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}