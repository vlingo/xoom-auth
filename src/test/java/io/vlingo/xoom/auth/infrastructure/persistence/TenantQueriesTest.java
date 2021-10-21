package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.infrastructure.TenantData;
import io.vlingo.xoom.auth.model.tenant.TenantId;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static io.vlingo.xoom.auth.test.Assertions.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TenantQueriesTest {

  private World world;
  private StateStore stateStore;
  private TenantQueries queries;

  @BeforeEach
  public void setUp() {
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, TenantData.class);
    queries = world.actorFor(TenantQueries.class, TenantQueriesActor.class, stateStore);
  }

  @AfterEach
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void itQueriesTheTenantById() {
    final TenantId firstTenantId = TenantId.from("f6f2402b-dd97-4b07-9b6b-55138b173606");
    final TenantId secondTenantId = TenantId.from("4120a2f7-89ff-479b-934c-6d0ac2e2e138");
    final TenantData firstTenant = TenantData.from(firstTenantId, "first-tenant-name", "first-tenant-description", true);
    final TenantData secondTenant = TenantData.from(secondTenantId, "second-tenant-name", "second-tenant-description", true);

    givenTenantsExist(firstTenant, secondTenant);

    assertCompletes(queries.tenantOf(firstTenantId), tenant -> assertEquals(firstTenant, tenant));
    assertCompletes(queries.tenantOf(secondTenantId), tenant -> assertEquals(secondTenant, tenant));
  }

  @Test
  public void itQueriesAllTenants() {
    final TenantId firstTenantId = TenantId.from("f6f2402b-dd97-4b07-9b6b-55138b173606");
    final TenantId secondTenantId = TenantId.from("4120a2f7-89ff-479b-934c-6d0ac2e2e138");
    final TenantData firstTenant = TenantData.from(firstTenantId, "first-tenant-name", "first-tenant-description", true);
    final TenantData secondTenant = TenantData.from(secondTenantId, "second-tenant-name", "second-tenant-description", true);

    givenTenantsExist(firstTenant, secondTenant);

    final Completes<Collection<TenantData>> outcome = queries.tenants();

    assertCompletes(outcome, tenants -> {
      assertContains(firstTenant, tenants);
      assertContains(secondTenant, tenants);
    });
  }

  @Test
  public void itReturnsAnEmptyTenantIfItIsNotFound() {
    final Completes<TenantData> result = queries.tenantOf(TenantId.from("48000827-a6c8-4a29-8dbc-f88a5fa57b58"));
    assertCompletes(result, tenant -> assertEquals("", tenant.tenantId));
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {
    }
  };

  private void givenTenantsExist(final TenantData... tenants) {
    Arrays.stream(tenants).forEach(tenant -> stateStore.write(tenant.tenantId, tenant, 1, NOOP_WRITER));
  }
}