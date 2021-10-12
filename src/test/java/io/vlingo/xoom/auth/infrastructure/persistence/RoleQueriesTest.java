package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.infrastructure.RoleData;
import io.vlingo.xoom.auth.model.role.RoleId;
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

  @Test
  public void queryById() {
    final RoleId firstRoleId = RoleId.from("06f22ad4-49f5-46a4-b350-f6198a7646a3", "first-role-name");
    final RoleData firstRole = RoleData.from(firstRoleId, "first-role-name", "first-role-description");
    final RoleId secondRoleId = RoleId.from("3f51d0fd-335e-41b0-b57c-766470cf6ad7", "second-role-name");
    final RoleData secondRole = RoleData.from(secondRoleId, "second-role-name", "second-role-description");

    stateStore.write(firstRole.id, firstRole, 1, NOOP_WRITER);
    stateStore.write(secondRole.id, secondRole, 1, NOOP_WRITER);

    final RoleData firstData = queries.roleOf(firstRoleId).await();

    assertEquals(firstRole.id, firstData.id);
    assertEquals(firstRole.tenantId, firstData.tenantId);
    assertEquals("first-role-name", firstData.name);
    assertEquals("first-role-description", firstData.description);

    final RoleData secondData = queries.roleOf(secondRoleId).await();

    assertEquals(secondRole.id, secondData.id);
    assertEquals(secondRole.tenantId, secondData.tenantId);
    assertEquals("second-role-name", secondData.name);
    assertEquals("second-role-description", secondData.description);
  }

  @Test
  public void queryAll() {
    final RoleId firstRoleId = RoleId.from("06f22ad4-49f5-46a4-b350-f6198a7646a3", "first-role-name");
    final RoleData firstRole = RoleData.from(firstRoleId, "first-role-name", "first-role-description");
    final RoleId secondRoleId = RoleId.from("3f51d0fd-335e-41b0-b57c-766470cf6ad7", "second-role-name");
    final RoleData secondRole = RoleData.from(secondRoleId, "second-role-name", "second-role-description");

    stateStore.write(firstRole.id, firstRole, 1, NOOP_WRITER);
    stateStore.write(secondRole.id, secondRole, 1, NOOP_WRITER);

    final Collection<RoleData> results = queries.roles().await();
    final RoleData firstData = results.stream().filter(data -> data.id.equals(firstRole.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstRole.id, firstData.id);
    assertEquals(firstRole.tenantId, firstData.tenantId);
    assertEquals("first-role-name", firstData.name);
    assertEquals("first-role-description", firstData.description);

    final RoleData secondData = results.stream().filter(data -> data.id.equals(secondRole.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondRole.id, secondData.id);
    assertEquals(secondRole.tenantId, secondData.tenantId);
    assertEquals("second-role-name", secondData.name);
    assertEquals("second-role-description", secondData.description);
  }

  @Test
  public void roleOfEmptyResult(){
    final RoleData result = queries.roleOf(RoleId.from("8e8c0fe5-c727-43a6-9307-926214a71af4", "role-c")).await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}