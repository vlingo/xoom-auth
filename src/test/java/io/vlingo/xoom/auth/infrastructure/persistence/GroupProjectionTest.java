package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.Projection;
import io.vlingo.xoom.lattice.model.projection.TextProjectable;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.vlingo.xoom.auth.model.group.*;
import io.vlingo.xoom.auth.infrastructure.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GroupProjectionTest {

  private World world;
  private StateStore stateStore;
  private Projection projection;
  private Map<String, String> valueToProjectionId;

  @BeforeEach
  public void setUp() {
    world = World.startWithDefaults("test-state-store-projection");
    NoOpDispatcher dispatcher = new NoOpDispatcher();
    valueToProjectionId = new ConcurrentHashMap<>();
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(dispatcher));
    StatefulTypeRegistry statefulTypeRegistry = StatefulTypeRegistry.registerAll(world, stateStore, GroupData.class);
    QueryModelStateStoreProvider.using(world.stage(), statefulTypeRegistry);
    projection = world.actorFor(Projection.class, GroupProjectionActor.class, stateStore);
  }

  private void registerExampleGroup(GroupState firstData, GroupState secondData) {
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(2);
    projection.projectWith(createGroupProvisioned(firstData), control);
    projection.projectWith(createGroupProvisioned(secondData), control);
  }


  private Projectable createGroupProvisioned(GroupState data) {
    final GroupProvisioned eventData = new GroupProvisioned(data.id, data.name, data.description, data.tenantId);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(GroupProvisioned.class, 1,
    JsonSerialization.serialized(eventData), 1, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

    @Test
    public void provisionGroup() {
      final GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
      final GroupData secondData = GroupData.from("2", "second-group-name", "second-group-description", "second-group-tenantId");
      final CountingProjectionControl control = new CountingProjectionControl();
      final AccessSafely access = control.afterCompleting(2);
      projection.projectWith(createGroupProvisioned(firstData.toGroupState()), control);
      projection.projectWith(createGroupProvisioned(secondData.toGroupState()), control);
      final Map<String, Integer> confirmations = access.readFrom("confirmations");

      assertEquals(2, confirmations.size());
      assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));
      assertEquals(1, valueOfProjectionIdFor(secondData.id, confirmations));

      CountingReadResultInterest interest = new CountingReadResultInterest();
      AccessSafely interestAccess = interest.afterCompleting(1);
      stateStore.read(firstData.id, GroupData.class, interest);
      GroupData item = interestAccess.readFrom("item", firstData.id);

        interest = new CountingReadResultInterest();
        interestAccess = interest.afterCompleting(1);
        stateStore.read(secondData.id, GroupData.class, interest);
        item = interestAccess.readFrom("item", secondData.id);
        assertEquals(secondData.id, item.id);
    }


  private Projectable createGroupDescriptionChanged(GroupState data) {
    final GroupDescriptionChanged eventData = new GroupDescriptionChanged(data.id, data.description, data.tenantId);
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(GroupDescriptionChanged.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

    @Test
    public void changeDescription() {
        final GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
        final GroupData secondData = GroupData.from("2", "second-group-name", "second-group-description", "second-group-tenantId");
      registerExampleGroup(firstData.toGroupState(), secondData.toGroupState());

      final CountingProjectionControl control = new CountingProjectionControl();
      final AccessSafely access = control.afterCompleting(1);
      projection.projectWith(createGroupDescriptionChanged(firstData.toGroupState()), control);
      final Map<String, Integer> confirmations = access.readFrom("confirmations");

      assertEquals(1, confirmations.size());
      assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

      CountingReadResultInterest interest = new CountingReadResultInterest();
      AccessSafely interestAccess = interest.afterCompleting(1);
      stateStore.read(firstData.id, GroupData.class, interest);
      GroupData item = interestAccess.readFrom("item", firstData.id);
    }


  private Projectable createGroupAssignedToGroup(GroupState data) {
    final GroupAssignedToGroup eventData = new GroupAssignedToGroup(data.id, data.tenantId);
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(GroupAssignedToGroup.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

    @Test
    public void assignGroup() {
        final GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
        final GroupData secondData = GroupData.from("2", "second-group-name", "second-group-description", "second-group-tenantId");
      registerExampleGroup(firstData.toGroupState(), secondData.toGroupState());

      final CountingProjectionControl control = new CountingProjectionControl();
      final AccessSafely access = control.afterCompleting(1);
      projection.projectWith(createGroupAssignedToGroup(firstData.toGroupState()), control);
      final Map<String, Integer> confirmations = access.readFrom("confirmations");

      assertEquals(1, confirmations.size());
      assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

      CountingReadResultInterest interest = new CountingReadResultInterest();
      AccessSafely interestAccess = interest.afterCompleting(1);
      stateStore.read(firstData.id, GroupData.class, interest);
      GroupData item = interestAccess.readFrom("item", firstData.id);
    }


  private Projectable createGroupUnassignedFromGroup(GroupState data) {
    final GroupUnassignedFromGroup eventData = new GroupUnassignedFromGroup(data.id, data.tenantId);
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(GroupUnassignedFromGroup.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

    @Test
    public void unassignGroup() {
        final GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
        final GroupData secondData = GroupData.from("2", "second-group-name", "second-group-description", "second-group-tenantId");
      registerExampleGroup(firstData.toGroupState(), secondData.toGroupState());

      final CountingProjectionControl control = new CountingProjectionControl();
      final AccessSafely access = control.afterCompleting(1);
      projection.projectWith(createGroupUnassignedFromGroup(firstData.toGroupState()), control);
      final Map<String, Integer> confirmations = access.readFrom("confirmations");

      assertEquals(1, confirmations.size());
      assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

      CountingReadResultInterest interest = new CountingReadResultInterest();
      AccessSafely interestAccess = interest.afterCompleting(1);
      stateStore.read(firstData.id, GroupData.class, interest);
      GroupData item = interestAccess.readFrom("item", firstData.id);
    }


  private Projectable createUserAssignedToGroup(GroupState data) {
    final UserAssignedToGroup eventData = new UserAssignedToGroup(data.id, data.tenantId);
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserAssignedToGroup.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

    @Test
    public void assignUser() {
        final GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
        final GroupData secondData = GroupData.from("2", "second-group-name", "second-group-description", "second-group-tenantId");
      registerExampleGroup(firstData.toGroupState(), secondData.toGroupState());

      final CountingProjectionControl control = new CountingProjectionControl();
      final AccessSafely access = control.afterCompleting(1);
      projection.projectWith(createUserAssignedToGroup(firstData.toGroupState()), control);
      final Map<String, Integer> confirmations = access.readFrom("confirmations");

      assertEquals(1, confirmations.size());
      assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

      CountingReadResultInterest interest = new CountingReadResultInterest();
      AccessSafely interestAccess = interest.afterCompleting(1);
      stateStore.read(firstData.id, GroupData.class, interest);
      GroupData item = interestAccess.readFrom("item", firstData.id);
    }


  private Projectable createUserUnassignedFromGroup(GroupState data) {
    final UserUnassignedFromGroup eventData = new UserUnassignedFromGroup(data.id, data.tenantId);
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserUnassignedFromGroup.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

    @Test
    public void unassignUser() {
        final GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
        final GroupData secondData = GroupData.from("2", "second-group-name", "second-group-description", "second-group-tenantId");
      registerExampleGroup(firstData.toGroupState(), secondData.toGroupState());

      final CountingProjectionControl control = new CountingProjectionControl();
      final AccessSafely access = control.afterCompleting(1);
      projection.projectWith(createUserUnassignedFromGroup(firstData.toGroupState()), control);
      final Map<String, Integer> confirmations = access.readFrom("confirmations");

      assertEquals(1, confirmations.size());
      assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

      CountingReadResultInterest interest = new CountingReadResultInterest();
      AccessSafely interestAccess = interest.afterCompleting(1);
      stateStore.read(firstData.id, GroupData.class, interest);
      GroupData item = interestAccess.readFrom("item", firstData.id);
    }

  private int valueOfProjectionIdFor(final String valueText, final Map<String, Integer> confirmations) {
    return confirmations.get(valueToProjectionId.get(valueText));
  }
}
