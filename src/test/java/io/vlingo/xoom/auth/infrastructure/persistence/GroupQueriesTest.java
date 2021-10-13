package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.auth.infrastructure.GroupData;
import io.vlingo.xoom.auth.model.group.GroupId;
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

  @Test
  public void queryById() {
    final GroupId firstGroupId = GroupId.from(TenantId.from("e79e02c5-735f-4998-b414-938479650be0"), "first-group-name");
    final GroupData firstGroup = GroupData.from(firstGroupId, "first-group-name", "first-group-description");
    final GroupId secondGroupId = GroupId.from(TenantId.from("96bf1fd1-9bdc-4352-99b4-8089e28cfaa3"), "second-group-name");
    final GroupData secondGroup = GroupData.from(secondGroupId, "second-group-name", "second-group-description");
    stateStore.write(firstGroup.id, firstGroup, 1, NOOP_WRITER);
    stateStore.write(secondGroup.id, secondGroup, 1, NOOP_WRITER);

    final GroupData firstData = queries.groupOf(firstGroupId).await();

    assertEquals("e79e02c5-735f-4998-b414-938479650be0:first-group-name", firstData.id);
    assertEquals("first-group-name", firstData.name);
    assertEquals("first-group-description", firstData.description);
    assertEquals("e79e02c5-735f-4998-b414-938479650be0", firstData.tenantId);

    final GroupData secondData = queries.groupOf(secondGroupId).await();

    assertEquals("96bf1fd1-9bdc-4352-99b4-8089e28cfaa3:second-group-name", secondData.id);
    assertEquals("second-group-name", secondData.name);
    assertEquals("second-group-description", secondData.description);
    assertEquals("96bf1fd1-9bdc-4352-99b4-8089e28cfaa3", secondData.tenantId);
  }

  @Test
  public void queryAll() {
    final GroupId firstGroupId = GroupId.from(TenantId.from("e79e02c5-735f-4998-b414-938479650be0"), "first-group-name");
    final GroupData firstGroup = GroupData.from(firstGroupId, "first-group-name", "first-group-description");
    final GroupId secondGroupId = GroupId.from(TenantId.from("96bf1fd1-9bdc-4352-99b4-8089e28cfaa3"), "second-group-name");
    final GroupData secondGroup = GroupData.from(secondGroupId, "second-group-name", "second-group-description");
    stateStore.write(firstGroup.id, firstGroup, 1, NOOP_WRITER);
    stateStore.write(secondGroup.id, secondGroup, 1, NOOP_WRITER);

    final Collection<GroupData> results = queries.groups().await();
    final GroupData firstData = results.stream().filter(data -> data.id.equals(firstGroup.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals("e79e02c5-735f-4998-b414-938479650be0:first-group-name", firstData.id);
    assertEquals("first-group-name", firstData.name);
    assertEquals("first-group-description", firstData.description);
    assertEquals("e79e02c5-735f-4998-b414-938479650be0", firstData.tenantId);

    final GroupData secondData = results.stream().filter(data -> data.id.equals(secondGroup.id)).findFirst().orElseThrow(RuntimeException::new);

    assertEquals("96bf1fd1-9bdc-4352-99b4-8089e28cfaa3:second-group-name", secondData.id);
    assertEquals("second-group-name", secondData.name);
    assertEquals("second-group-description", secondData.description);
    assertEquals("96bf1fd1-9bdc-4352-99b4-8089e28cfaa3", secondData.tenantId);
  }

  @Test
  public void groupOfEmptyResult(){
    final GroupData result = queries.groupOf(GroupId.from(TenantId.from("1"), "G2")).await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };

}