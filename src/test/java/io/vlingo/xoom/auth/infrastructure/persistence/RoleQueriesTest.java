package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.infrastructure.RoleData;
import io.vlingo.xoom.auth.model.role.RoleId;
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

public class RoleQueriesTest {

  private World world;
  private StateStore stateStore;
  private RoleQueries queries;

  @BeforeEach
  public void setUp() {
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, RoleData.class);
    queries = world.actorFor(RoleQueries.class, RoleQueriesActor.class, stateStore);
  }

  @AfterEach
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void itQueriesTheRoleById() {
    final RoleId firstRoleId = RoleId.from(TenantId.from("06f22ad4-49f5-46a4-b350-f6198a7646a3"), "first-role-name");
    final RoleData firstRole = RoleData.from(firstRoleId, "first-role-name", "first-role-description");
    final RoleId secondRoleId = RoleId.from(TenantId.from("3f51d0fd-335e-41b0-b57c-766470cf6ad7"), "second-role-name");
    final RoleData secondRole = RoleData.from(secondRoleId, "second-role-name", "second-role-description");

    givenRolesExist(firstRole, secondRole);

    assertCompletes(queries.roleOf(firstRoleId), role -> assertEquals(firstRole, role));
    assertCompletes(queries.roleOf(secondRoleId), role -> assertEquals(secondRole, role));
  }

  @Test
  public void itQueriesAllRoles() {
    final RoleId firstRoleId = RoleId.from(TenantId.from("06f22ad4-49f5-46a4-b350-f6198a7646a3"), "first-role-name");
    final RoleData firstRole = RoleData.from(firstRoleId, "first-role-name", "first-role-description");
    final RoleId secondRoleId = RoleId.from(TenantId.from("3f51d0fd-335e-41b0-b57c-766470cf6ad7"), "second-role-name");
    final RoleData secondRole = RoleData.from(secondRoleId, "second-role-name", "second-role-description");

    givenRolesExist(firstRole, secondRole);

    final Completes<Collection<RoleData>> outcome = queries.roles();

    assertCompletes(outcome, roles -> {
      assertContains(firstRole, roles);
      assertContains(secondRole, roles);
    });
  }

  @Test
  public void itReturnsAnEmptyRoleIfItIsNotFound() {
    final Completes<RoleData> result = queries.roleOf(RoleId.from(TenantId.from("8e8c0fe5-c727-43a6-9307-926214a71af4"), "role-c"));

    assertCompletes(result, role -> assertEquals("", role.id));
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {
    }
  };

  private void givenRolesExist(final RoleData... roles) {
    Arrays.stream(roles).forEach(role -> stateStore.write(role.id, role, 1, NOOP_WRITER));
  }
}