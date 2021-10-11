package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.infrastructure.GroupData;
import io.vlingo.xoom.auth.model.group.GroupId;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

  private static final GroupData FIRST_QUERY_BY_ID_TEST_DATA = GroupData.from(GroupId.from("e79e02c5-735f-4998-b414-938479650be0", "first-group-name"), "first-group-name", "first-group-description");
  private static final GroupData SECOND_QUERY_BY_ID_TEST_DATA = GroupData.from(GroupId.from("96bf1fd1-9bdc-4352-99b4-8089e28cfaa3", "second-group-name"), "second-group-name", "second-group-description");

  @Test
  public void queryById() {
    stateStore.write(FIRST_QUERY_BY_ID_TEST_DATA.id, FIRST_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write(SECOND_QUERY_BY_ID_TEST_DATA.id, SECOND_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);

    final GroupData firstData = queries.groupOf(GroupId.from("e79e02c5-735f-4998-b414-938479650be0", "first-group-name")).await();

    assertEquals(firstData.id, "e79e02c5-735f-4998-b414-938479650be0:first-group-name");
    assertEquals(firstData.name, "first-group-name");
    assertEquals(firstData.description, "first-group-description");
    assertEquals(firstData.tenantId, "e79e02c5-735f-4998-b414-938479650be0");

    final GroupData secondData = queries.groupOf(GroupId.from("96bf1fd1-9bdc-4352-99b4-8089e28cfaa3", "second-group-name")).await();

    assertEquals(secondData.id, "96bf1fd1-9bdc-4352-99b4-8089e28cfaa3:second-group-name");
    assertEquals(secondData.name, "second-group-name");
    assertEquals(secondData.description, "second-group-description");
    assertEquals(secondData.tenantId, "96bf1fd1-9bdc-4352-99b4-8089e28cfaa3");
  }

  private static final GroupData FIRST_QUERY_ALL_TEST_DATA = GroupData.from(GroupId.from("28819da6-b36c-45a6-9193-727d41a94dde", "first-group-name"), "first-group-name", "first-group-description");
  private static final GroupData SECOND_QUERY_ALL_TEST_DATA = GroupData.from(GroupId.from("d419a592-996a-41c2-979d-2a6972cf5c05", "second-group-name"), "second-group-name", "second-group-description");

  @Test
  public void queryAll() {
    stateStore.write(FIRST_QUERY_ALL_TEST_DATA.id, FIRST_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write(SECOND_QUERY_BY_ID_TEST_DATA.id, SECOND_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);

    final Collection<GroupData> results = queries.groups().await();
    final GroupData firstData = results.stream().filter(data -> data.id.equals("28819da6-b36c-45a6-9193-727d41a94dde:first-group-name")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstData.id, "28819da6-b36c-45a6-9193-727d41a94dde:first-group-name");
    assertEquals(firstData.name, "first-group-name");
    assertEquals(firstData.description, "first-group-description");
    assertEquals(firstData.tenantId, "28819da6-b36c-45a6-9193-727d41a94dde");

    final GroupData secondData = results.stream().filter(data -> data.id.equals("d419a592-996a-41c2-979d-2a6972cf5c05:second-group-name")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondData.id, "d419a592-996a-41c2-979d-2a6972cf5c05:second-group-name");
    assertEquals(secondData.name, "second-group-name");
    assertEquals(secondData.description, "second-group-description");
    assertEquals(secondData.tenantId, "d419a592-996a-41c2-979d-2a6972cf5c05");
  }

  @Test
  public void groupOfEmptyResult(){
    final GroupData result = queries.groupOf(GroupId.from("1", "G2")).await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}