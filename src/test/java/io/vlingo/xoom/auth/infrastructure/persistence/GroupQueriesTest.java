package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.infrastructure.GroupData;
import io.vlingo.xoom.auth.model.group.GroupId;
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

public class GroupQueriesTest {

  private World world;
  private StateStore stateStore;
  private GroupQueries queries;

  @BeforeEach
  public void setUp() {
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, GroupData.class);
    queries = world.actorFor(GroupQueries.class, GroupQueriesActor.class, stateStore);
  }

  @AfterEach
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void itQueriesTheGroupById() {
    final GroupId firstGroupId = GroupId.from(TenantId.from("e79e02c5-735f-4998-b414-938479650be0"), "first-group-name");
    final GroupData firstGroup = GroupData.from(firstGroupId, "first-group-name", "first-group-description");
    final GroupId secondGroupId = GroupId.from(TenantId.from("96bf1fd1-9bdc-4352-99b4-8089e28cfaa3"), "second-group-name");
    final GroupData secondGroup = GroupData.from(secondGroupId, "second-group-name", "second-group-description");

    givenGroupsExist(firstGroup, secondGroup);

    assertCompletes(queries.groupOf(firstGroupId), group -> assertEquals(firstGroup, group));
    assertCompletes(queries.groupOf(secondGroupId), group -> assertEquals(secondGroup, group));
  }

  @Test
  public void itQueriesAllGroups() {
    final GroupId firstGroupId = GroupId.from(TenantId.from("e79e02c5-735f-4998-b414-938479650be0"), "first-group-name");
    final GroupData firstGroup = GroupData.from(firstGroupId, "first-group-name", "first-group-description");
    final GroupId secondGroupId = GroupId.from(TenantId.from("96bf1fd1-9bdc-4352-99b4-8089e28cfaa3"), "second-group-name");
    final GroupData secondGroup = GroupData.from(secondGroupId, "second-group-name", "second-group-description");

    givenGroupsExist(firstGroup, secondGroup);

    final Completes<Collection<GroupData>> outcome = queries.groups();

    assertCompletes(outcome, groups -> {
      assertContains(firstGroup, groups);
      assertContains(secondGroup, groups);
    });
  }

  @Test
  public void itReturnsAnEmptyGroupIfItIsNotFound() {
    final Completes<GroupData> result = queries.groupOf(GroupId.from(TenantId.from("5e39f013-27f4-434d-8f8a-dba940baed7c"), "G2"));

    assertCompletes(result, group -> assertEquals("", group.id));
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {
    }
  };

  private void givenGroupsExist(final GroupData... groups) {
    Arrays.stream(groups).forEach(group -> stateStore.write(group.id, group, 1, NOOP_WRITER));
  }
}