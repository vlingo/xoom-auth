package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.model.permission.PermissionId;
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

public class PermissionQueriesTest {

  private World world;
  private StateStore stateStore;
  private PermissionQueries queries;

  @BeforeEach
  public void setUp(){
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, PermissionData.class);
    queries = world.actorFor(PermissionQueries.class, PermissionQueriesActor.class, stateStore);
  }

  private static final PermissionData FIRST_QUERY_BY_ID_TEST_DATA = PermissionData.from(PermissionId.from("8844bb24-811a-45c7-b98b-ba7a88a42372",  "first-permission-name"), new HashSet<>(),  "first-permission-name", "first-permission-description");
  private static final PermissionData SECOND_QUERY_BY_ID_TEST_DATA = PermissionData.from(PermissionId.from("2f50fc24-85b1-4657-b876-82491bfc3a70", "second-permission-name"), new HashSet<>(), "second-permission-name", "second-permission-description");

  @Test
  public void queryById() {
    stateStore.write(FIRST_QUERY_BY_ID_TEST_DATA.id, FIRST_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write(SECOND_QUERY_BY_ID_TEST_DATA.id, SECOND_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);

    final PermissionData firstData = queries.permissionOf(FIRST_QUERY_BY_ID_TEST_DATA.id).await();

    assertEquals(firstData.id, FIRST_QUERY_BY_ID_TEST_DATA.id);
    assertNotNull(firstData.constraints);
    assertEquals(firstData.description, "first-permission-description");
    assertEquals(firstData.name, "first-permission-name");
    assertEquals(firstData.tenantId, "8844bb24-811a-45c7-b98b-ba7a88a42372");

    final PermissionData secondData = queries.permissionOf(SECOND_QUERY_BY_ID_TEST_DATA.id).await();

    assertEquals(secondData.id, SECOND_QUERY_BY_ID_TEST_DATA.id);
    assertNotNull(secondData.constraints);
    assertEquals(secondData.description, "second-permission-description");
    assertEquals(secondData.name, "second-permission-name");
    assertEquals(secondData.tenantId, "2f50fc24-85b1-4657-b876-82491bfc3a70");
  }

  private static final PermissionData FIRST_QUERY_ALL_TEST_DATA = PermissionData.from(PermissionId.from("8844bb24-811a-45c7-b98b-ba7a88a42372",  "first-permission-name"), new HashSet<>(),  "first-permission-name", "first-permission-description");
  private static final PermissionData SECOND_QUERY_ALL_TEST_DATA = PermissionData.from(PermissionId.from("2f50fc24-85b1-4657-b876-82491bfc3a70", "second-permission-name"), new HashSet<>(), "second-permission-name", "second-permission-description");

  @Test
  public void queryAll() {
    stateStore.write(FIRST_QUERY_ALL_TEST_DATA.id, FIRST_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write(SECOND_QUERY_ALL_TEST_DATA.id, SECOND_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);

    final Collection<PermissionData> results = queries.permissions().await();
    final PermissionData firstData = results.stream().filter(data -> data.id.equals(FIRST_QUERY_ALL_TEST_DATA.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstData.id, FIRST_QUERY_ALL_TEST_DATA.id);
    assertNotNull(firstData.constraints);
    assertEquals(firstData.description, "first-permission-description");
    assertEquals(firstData.name, "first-permission-name");
    assertEquals(firstData.tenantId, "8844bb24-811a-45c7-b98b-ba7a88a42372");

    final PermissionData secondData = results.stream().filter(data -> data.id.equals(SECOND_QUERY_BY_ID_TEST_DATA.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondData.id, SECOND_QUERY_ALL_TEST_DATA.id);
    assertNotNull(secondData.constraints);
    assertEquals(secondData.description, "second-permission-description");
    assertEquals(secondData.name, "second-permission-name");
    assertEquals(secondData.tenantId, "2f50fc24-85b1-4657-b876-82491bfc3a70");
  }

  @Test
  public void permissionOfEmptyResult(){
    final PermissionData result = queries.permissionOf("1").await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}