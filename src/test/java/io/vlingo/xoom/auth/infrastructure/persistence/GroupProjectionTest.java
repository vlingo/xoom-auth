package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.vlingo.xoom.auth.model.group.*;
import io.vlingo.xoom.auth.infrastructure.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GroupProjectionTest {

  private final TenantId TENANT_ID = TenantId.from("47f36e24-9723-49cf-be71-54afc03aa120");
  private final String FIRST_GROUP_NAME = "Group A";
  private final String FIRST_GROUP_DESCRIPTION = "Group A description";
  private final GroupId FIRST_GROUP_ID = GroupId.from(TENANT_ID, FIRST_GROUP_NAME);
  private final String SECOND_GROUP_NAME = "Group B";
  private final String SECOND_GROUP_DESCRIPTION = "Group B description";
  private final GroupId SECOND_GROUP_ID = GroupId.from(TENANT_ID, SECOND_GROUP_NAME);

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

  @Test
  public void provisionGroup() {
    final GroupData firstData = GroupData.from(FIRST_GROUP_ID, FIRST_GROUP_NAME, FIRST_GROUP_DESCRIPTION);
    final GroupData secondData = GroupData.from(SECOND_GROUP_ID, SECOND_GROUP_NAME, SECOND_GROUP_DESCRIPTION);

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

    assertEquals(FIRST_GROUP_ID.idString(), item.id);
    assertEquals(FIRST_GROUP_NAME, item.name);
    assertEquals(FIRST_GROUP_DESCRIPTION, item.description);
    assertEquals(TENANT_ID.id, item.tenantId);

    interest = new CountingReadResultInterest();
    interestAccess = interest.afterCompleting(1);
    stateStore.read(secondData.id, GroupData.class, interest);
    item = interestAccess.readFrom("item", secondData.id);
    assertEquals(secondData.id, item.id);
    assertEquals(SECOND_GROUP_ID.idString(), item.id);
    assertEquals(SECOND_GROUP_NAME, item.name);
    assertEquals(SECOND_GROUP_DESCRIPTION, item.description);
    assertEquals(TENANT_ID.id, item.tenantId);
  }

  @Test
  public void changeDescription() {
    final GroupData firstData = GroupData.from(FIRST_GROUP_ID, FIRST_GROUP_NAME, FIRST_GROUP_DESCRIPTION);
    final GroupData secondData = GroupData.from(SECOND_GROUP_ID, SECOND_GROUP_NAME, SECOND_GROUP_DESCRIPTION);
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

    assertEquals(FIRST_GROUP_ID.idString(), item.id);
    assertEquals(FIRST_GROUP_DESCRIPTION, item.description);
    assertEquals(TENANT_ID.id, item.tenantId);
  }

  @Test
  public void assignGroup() {
    final GroupData firstData = GroupData.from(FIRST_GROUP_ID, FIRST_GROUP_NAME, FIRST_GROUP_DESCRIPTION);
    final GroupData secondData = GroupData.from(SECOND_GROUP_ID, SECOND_GROUP_NAME, SECOND_GROUP_DESCRIPTION);
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

    assertEquals(FIRST_GROUP_ID.idString(), item.id);
    assertEquals(TENANT_ID.id, item.tenantId);
  }

  @Test
  public void unassignGroup() {
    final GroupData firstData = GroupData.from(FIRST_GROUP_ID, FIRST_GROUP_NAME, FIRST_GROUP_DESCRIPTION);
    final GroupData secondData = GroupData.from(SECOND_GROUP_ID, SECOND_GROUP_NAME, SECOND_GROUP_DESCRIPTION);
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

    assertEquals(FIRST_GROUP_ID.idString(), item.id);
    assertEquals(TENANT_ID.id, item.tenantId);
  }

  @Test
  public void assignUser() {
    final GroupData firstData = GroupData.from(FIRST_GROUP_ID, FIRST_GROUP_NAME, FIRST_GROUP_DESCRIPTION);
    final GroupData secondData = GroupData.from(SECOND_GROUP_ID, SECOND_GROUP_NAME, SECOND_GROUP_DESCRIPTION);
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

    assertEquals(FIRST_GROUP_ID.idString(), item.id);
    assertEquals(TENANT_ID.id, item.tenantId);
  }

  @Test
  public void unassignUser() {
    final GroupData firstData = GroupData.from(FIRST_GROUP_ID, FIRST_GROUP_NAME, FIRST_GROUP_DESCRIPTION);
    final GroupData secondData = GroupData.from(SECOND_GROUP_ID, SECOND_GROUP_NAME, SECOND_GROUP_DESCRIPTION);
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

    assertEquals(FIRST_GROUP_ID.idString(), item.id);
    assertEquals(TENANT_ID.id, item.tenantId);
  }

  private int valueOfProjectionIdFor(final String valueText, final Map<String, Integer> confirmations) {
    return confirmations.get(valueToProjectionId.get(valueText));
  }

  private Projectable createGroupProvisioned(GroupState data) {
    final GroupProvisioned eventData = new GroupProvisioned(data.id, data.name, data.description);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(GroupProvisioned.class, 1, JsonSerialization.serialized(eventData), 1, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createGroupDescriptionChanged(GroupState data) {
    final GroupDescriptionChanged eventData = new GroupDescriptionChanged(data.id, data.description);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(GroupDescriptionChanged.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createGroupAssignedToGroup(GroupState data) {
    final GroupAssignedToGroup eventData = new GroupAssignedToGroup(data.id, GroupId.from(data.id.tenantId, "inner-group"));

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(GroupAssignedToGroup.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createGroupUnassignedFromGroup(GroupState data) {
    final GroupUnassignedFromGroup eventData = new GroupUnassignedFromGroup(data.id, GroupId.from(data.id.tenantId, "inner-group"));

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(GroupUnassignedFromGroup.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createUserAssignedToGroup(GroupState data) {
    final UserId userId = UserId.from(TenantId.unique(), "bobby");
    final UserAssignedToGroup eventData = new UserAssignedToGroup(data.id, userId);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserAssignedToGroup.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createUserUnassignedFromGroup(GroupState data) {
    final UserId userId = UserId.from(TenantId.unique(), "bobby");
    final UserUnassignedFromGroup eventData = new UserUnassignedFromGroup(data.id, userId);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserUnassignedFromGroup.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

}
