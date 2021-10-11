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
  public void setUp() {
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, PermissionData.class);
    queries = world.actorFor(PermissionQueries.class, PermissionQueriesActor.class, stateStore);
  }

  @Test
  public void queryById() {
    final PermissionId firstPermissionId = PermissionId.from("8844bb24-811a-45c7-b98b-ba7a88a42372", "first-permission-name");
    final PermissionData firstPermission = PermissionData.from(firstPermissionId, new HashSet<>(),  "first-permission-name", "first-permission-description");
    final PermissionId secondPermissionId = PermissionId.from("2f50fc24-85b1-4657-b876-82491bfc3a70", "second-permission-name");
    final PermissionData secondPermission = PermissionData.from(secondPermissionId, new HashSet<>(), "second-permission-name", "second-permission-description");

    stateStore.write(firstPermission.id, firstPermission, 1, NOOP_WRITER);
    stateStore.write(secondPermission.id, secondPermission, 1, NOOP_WRITER);

    final PermissionData firstData = queries.permissionOf(firstPermissionId).await();

    assertEquals(firstPermission.id, firstData.id);
    assertNotNull(firstData.constraints);
    assertEquals("first-permission-description", firstData.description);
    assertEquals("first-permission-name", firstData.name);
    assertEquals(firstPermissionId.tenantId, firstData.tenantId);

    final PermissionData secondData = queries.permissionOf(secondPermissionId).await();

    assertEquals(secondPermission.id, secondData.id);
    assertNotNull(secondData.constraints);
    assertEquals("second-permission-description", secondData.description);
    assertEquals("second-permission-name", secondData.name);
    assertEquals(secondPermissionId.tenantId, secondData.tenantId);
  }

  @Test
  public void queryAll() {
    final PermissionId firstPermissionId = PermissionId.from("8844bb24-811a-45c7-b98b-ba7a88a42372", "first-permission-name");
    final PermissionData firstPermission = PermissionData.from(firstPermissionId, new HashSet<>(),  "first-permission-name", "first-permission-description");
    final PermissionId secondPermissionId = PermissionId.from("2f50fc24-85b1-4657-b876-82491bfc3a70", "second-permission-name");
    final PermissionData secondPermission = PermissionData.from(secondPermissionId, new HashSet<>(), "second-permission-name", "second-permission-description");

    stateStore.write(firstPermission.id, firstPermission, 1, NOOP_WRITER);
    stateStore.write(secondPermission.id, secondPermission, 1, NOOP_WRITER);

    final Collection<PermissionData> results = queries.permissions().await();
    final PermissionData firstData = results.stream().filter(data -> data.id.equals(firstPermission.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstPermission.id, firstData.id);
    assertNotNull(firstData.constraints);
    assertEquals("first-permission-description", firstData.description);
    assertEquals("first-permission-name", firstData.name);
    assertEquals(firstPermissionId.tenantId, firstData.tenantId);

    final PermissionData secondData = results.stream().filter(data -> data.id.equals(secondPermission.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondPermission.id, secondData.id);
    assertNotNull(secondData.constraints);
    assertEquals("second-permission-description", secondData.description);
    assertEquals("second-permission-name", secondData.name);
    assertEquals(secondPermissionId.tenantId, secondData.tenantId);
  }

  @Test
  public void permissionOfEmptyResult() {
    final PermissionData result = queries.permissionOf(PermissionId.from("1", "P1")).await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}