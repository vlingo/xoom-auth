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

public class TenantQueriesTest {

  private World world;
  private StateStore stateStore;
  private TenantQueries queries;

  @BeforeEach
  public void setUp(){
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, TenantData.class);
    queries = world.actorFor(TenantQueries.class, TenantQueriesActor.class, stateStore);
  }

  private static final TenantData FIRST_QUERY_BY_ID_TEST_DATA = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
  private static final TenantData SECOND_QUERY_BY_ID_TEST_DATA = TenantData.from("2", "second-tenant-name", "second-tenant-description", true);

  @Test
  public void queryById() {
    stateStore.write("1", FIRST_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write("2", SECOND_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);

    final TenantData firstData = queries.tenantOf("1").await();

    assertEquals(firstData.id, "1");
    assertEquals(firstData.name, "first-tenant-name");
    assertEquals(firstData.description, "first-tenant-description");
    assertEquals(firstData.active, true);

    final TenantData secondData = queries.tenantOf("2").await();

    assertEquals(secondData.id, "2");
    assertEquals(secondData.name, "second-tenant-name");
    assertEquals(secondData.description, "second-tenant-description");
    assertEquals(secondData.active, true);
  }

  private static final TenantData FIRST_QUERY_ALL_TEST_DATA = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
  private static final TenantData SECOND_QUERY_ALL_TEST_DATA = TenantData.from("2", "second-tenant-name", "second-tenant-description", true);

  @Test
  public void queryAll() {
    stateStore.write("1", FIRST_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write("2", SECOND_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);

    final Collection<TenantData> results = queries.tenants().await();
    final TenantData firstData = results.stream().filter(data -> data.id.equals("1")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstData.id, "1");
    assertEquals(firstData.name, "first-tenant-name");
    assertEquals(firstData.description, "first-tenant-description");
    assertEquals(firstData.active, true);

    final TenantData secondData = results.stream().filter(data -> data.id.equals("2")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondData.id, "2");
    assertEquals(secondData.name, "second-tenant-name");
    assertEquals(secondData.description, "second-tenant-description");
    assertEquals(secondData.active, true);
  }

  @Test
  public void tenantOfEmptyResult(){
    final TenantData result = queries.tenantOf("1").await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}