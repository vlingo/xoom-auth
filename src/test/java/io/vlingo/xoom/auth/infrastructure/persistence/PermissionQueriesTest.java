package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.model.permission.PermissionId;
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

import java.util.*;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static io.vlingo.xoom.auth.test.Assertions.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PermissionQueriesTest {

  private World world;
  private StateStore stateStore;
  private PermissionQueries queries;

  @BeforeEach
  public void setUp() {
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, PermissionView.class);
    queries = world.actorFor(PermissionQueries.class, PermissionQueriesActor.class, stateStore);
  }

  @AfterEach
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void itQueriesThePermissionById() {
    final PermissionId firstPermissionId = PermissionId.from(TenantId.from("8844bb24-811a-45c7-b98b-ba7a88a42372"), "first-permission-name");
    final PermissionView firstPermission = PermissionView.from(firstPermissionId, new HashSet<>(), "first-permission-name", "first-permission-description");
    final PermissionId secondPermissionId = PermissionId.from(TenantId.from("2f50fc24-85b1-4657-b876-82491bfc3a70"), "second-permission-name");
    final PermissionView secondPermission = PermissionView.from(secondPermissionId, new HashSet<>(), "second-permission-name", "second-permission-description");

    givenPermissionsExist(firstPermission, secondPermission);

    assertCompletes(queries.permissionOf(firstPermissionId), permission -> assertEquals(firstPermission, permission));
    assertCompletes(queries.permissionOf(secondPermissionId), permission -> assertEquals(secondPermission, permission));
  }

  @Test
  public void itQueriesAllPermissions() {
    final PermissionId firstPermissionId = PermissionId.from(TenantId.from("8844bb24-811a-45c7-b98b-ba7a88a42372"), "first-permission-name");
    final PermissionView firstPermission = PermissionView.from(firstPermissionId, new HashSet<>(), "first-permission-name", "first-permission-description");
    final PermissionId secondPermissionId = PermissionId.from(TenantId.from("2f50fc24-85b1-4657-b876-82491bfc3a70"), "second-permission-name");
    final PermissionView secondPermission = PermissionView.from(secondPermissionId, new HashSet<>(), "second-permission-name", "second-permission-description");

    givenPermissionsExist(firstPermission, secondPermission);

    final Completes<Collection<PermissionView>> outcome = queries.permissions();

    assertCompletes(outcome, permissions -> {
      assertContains(firstPermission, permissions);
      assertContains(secondPermission, permissions);
    });
  }

  @Test
  public void itReturnsAnEmptyPermissionIfItIsNotFound() {
    final Completes<PermissionView> result = queries.permissionOf(PermissionId.from(TenantId.from("02e46626-a06c-483d-a4dd-dd829c918a83"), "P1"));

    assertCompletes(result, permission -> assertEquals("", permission.id));
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

  private void givenPermissionsExist(final PermissionView... permissions) {
    Arrays.stream(permissions).forEach(permission -> stateStore.write(permission.id, permission, 1, NOOP_WRITER));
  }
}