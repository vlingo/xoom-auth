package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.model.tenant.TenantId;
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

import java.util.*;

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

  @Test
  public void queryById() {
    final TenantId firstTenantId = TenantId.from("f6f2402b-dd97-4b07-9b6b-55138b173606");
    final TenantId secondTenantId = TenantId.from("4120a2f7-89ff-479b-934c-6d0ac2e2e138");
    final TenantData firstTenant = TenantData.from(firstTenantId, "first-tenant-name", "first-tenant-description", true);
    final TenantData secondTenant = TenantData.from(secondTenantId, "second-tenant-name", "second-tenant-description", true);
    stateStore.write(firstTenantId.idString(), firstTenant, 1, NOOP_WRITER);
    stateStore.write(secondTenantId.id, secondTenant, 1, NOOP_WRITER);

    final TenantData firstData = queries.tenantOf(firstTenantId).await();

    assertEquals(firstTenantId.id, firstData.tenantId);
    assertEquals("first-tenant-name", firstData.name);
    assertEquals("first-tenant-description", firstData.description);
    assertTrue(firstData.active);

    final TenantData secondData = queries.tenantOf(secondTenantId).await();

    assertEquals(secondTenantId.id, secondData.tenantId);
    assertEquals("second-tenant-name", secondData.name);
    assertEquals("second-tenant-description", secondData.description);
    assertTrue(secondData.active);
  }

  @Test
  public void queryAll() {
    final TenantId firstTenantId = TenantId.from("f6f2402b-dd97-4b07-9b6b-55138b173606");
    final TenantId secondTenantId = TenantId.from("4120a2f7-89ff-479b-934c-6d0ac2e2e138");
    final TenantData firstTenant = TenantData.from(firstTenantId, "first-tenant-name", "first-tenant-description", true);
    final TenantData secondTenant = TenantData.from(secondTenantId, "second-tenant-name", "second-tenant-description", true);
    stateStore.write(firstTenantId.idString(), firstTenant, 1, NOOP_WRITER);
    stateStore.write(secondTenantId.id, secondTenant, 1, NOOP_WRITER);

    final Collection<TenantData> results = queries.tenants().await();
    final TenantData firstData = results.stream().filter(data -> data.tenantId.equals(firstTenantId.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstTenantId.id, firstData.tenantId);
    assertEquals("first-tenant-name", firstData.name);
    assertEquals("first-tenant-description", firstData.description);
    assertTrue(firstData.active);

    final TenantData secondData = results.stream().filter(data -> data.tenantId.equals(secondTenantId.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondTenantId.id, secondData.tenantId);
    assertEquals("second-tenant-name", secondData.name);
    assertEquals("second-tenant-description", secondData.description);
    assertTrue(secondData.active);
  }

  @Test
  public void tenantOfEmptyResult(){
    final TenantData result = queries.tenantOf(TenantId.from("48000827-a6c8-4a29-8dbc-f88a5fa57b58")).await();
    assertEquals("", result.tenantId);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}