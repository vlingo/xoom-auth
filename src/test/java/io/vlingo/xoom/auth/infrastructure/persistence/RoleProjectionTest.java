package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.RoleData;
import io.vlingo.xoom.auth.model.role.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleProjectionTest {

  private final String TENANT_ID = "568c04ad-dd07-437f-bfb4-280b178f59f9";
  private final String FIRST_ROLE_NAME = "role-a";
  private final String FIRST_ROLE_DESCRIPTION = "Role A description";
  private final RoleId FIRST_ROLE_ID = RoleId.from(TENANT_ID, FIRST_ROLE_NAME);
  private final String SECOND_ROLE_NAME = "role-b";
  private final String SECOND_ROLE_DESCRIPTION = "Role B description";
  private final RoleId SECOND_ROLE_ID = RoleId.from(TENANT_ID, SECOND_ROLE_NAME);

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
    StatefulTypeRegistry statefulTypeRegistry = StatefulTypeRegistry.registerAll(world, stateStore, RoleData.class);
    QueryModelStateStoreProvider.using(world.stage(), statefulTypeRegistry);
    projection = world.actorFor(Projection.class, RoleProjectionActor.class, stateStore);
  }

  private void registerExampleRole(RoleState firstData, RoleState secondData) {
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(2);
    projection.projectWith(createRoleProvisioned(firstData), control);
    projection.projectWith(createRoleProvisioned(secondData), control);
  }

  @Test
  public void provisionRole() {
    final RoleData firstData = RoleData.from(FIRST_ROLE_ID, FIRST_ROLE_NAME, FIRST_ROLE_DESCRIPTION);
    final RoleData secondData = RoleData.from(SECOND_ROLE_ID, SECOND_ROLE_NAME, SECOND_ROLE_DESCRIPTION);

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(2);
    projection.projectWith(createRoleProvisioned(firstData.toRoleState()), control);
    projection.projectWith(createRoleProvisioned(secondData.toRoleState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(2, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));
    assertEquals(1, valueOfProjectionIdFor(secondData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, RoleData.class, interest);
    RoleData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_ROLE_ID.idString(), item.id);
    assertEquals(FIRST_ROLE_ID.tenantId, item.tenantId);
    assertEquals(FIRST_ROLE_NAME, item.name);
    assertEquals(FIRST_ROLE_DESCRIPTION, item.description);

    interest = new CountingReadResultInterest();
    interestAccess = interest.afterCompleting(1);
    stateStore.read(secondData.id, RoleData.class, interest);
    item = interestAccess.readFrom("item", secondData.id);
    assertEquals(SECOND_ROLE_ID.idString(), item.id);
    assertEquals(SECOND_ROLE_ID.tenantId, item.tenantId);
    assertEquals(SECOND_ROLE_NAME, item.name);
    assertEquals(SECOND_ROLE_DESCRIPTION, item.description);
  }

  @Test
  public void changeDescription() {
    final RoleData firstData = RoleData.from(FIRST_ROLE_ID, FIRST_ROLE_NAME, FIRST_ROLE_DESCRIPTION);
    final RoleData secondData = RoleData.from(SECOND_ROLE_ID, SECOND_ROLE_NAME, SECOND_ROLE_DESCRIPTION);
    registerExampleRole(firstData.toRoleState(), secondData.toRoleState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createRoleDescriptionChanged(firstData.toRoleState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, RoleData.class, interest);
    RoleData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_ROLE_ID.idString(), item.id);
    assertEquals(FIRST_ROLE_ID.tenantId, item.tenantId);
    assertEquals(FIRST_ROLE_NAME, item.name);
    assertEquals(FIRST_ROLE_DESCRIPTION, item.description);
  }

  @Test
  public void assignGroup() {
    final RoleData firstData = RoleData.from(FIRST_ROLE_ID, FIRST_ROLE_NAME, FIRST_ROLE_DESCRIPTION);
    final RoleData secondData = RoleData.from(SECOND_ROLE_ID, SECOND_ROLE_NAME, SECOND_ROLE_DESCRIPTION);
    registerExampleRole(firstData.toRoleState(), secondData.toRoleState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createGroupAssignedToRole(firstData.toRoleState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, RoleData.class, interest);
    RoleData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_ROLE_ID.idString(), item.id);
    assertEquals(FIRST_ROLE_ID.tenantId, item.tenantId);
    assertEquals(FIRST_ROLE_NAME, item.name);
    assertEquals(FIRST_ROLE_DESCRIPTION, item.description);
  }

  @Test
  public void unassignGroup() {
    final RoleData firstData = RoleData.from(FIRST_ROLE_ID, FIRST_ROLE_NAME, FIRST_ROLE_DESCRIPTION);
    final RoleData secondData = RoleData.from(SECOND_ROLE_ID, SECOND_ROLE_NAME, SECOND_ROLE_DESCRIPTION);
    registerExampleRole(firstData.toRoleState(), secondData.toRoleState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createGroupUnassignedFromRole(firstData.toRoleState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, RoleData.class, interest);
    RoleData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_ROLE_ID.idString(), item.id);
    assertEquals(FIRST_ROLE_ID.tenantId, item.tenantId);
    assertEquals(FIRST_ROLE_NAME, item.name);
    assertEquals(FIRST_ROLE_DESCRIPTION, item.description);
  }

  @Test
  public void assignUser() {
    final RoleData firstData = RoleData.from(FIRST_ROLE_ID, FIRST_ROLE_NAME, FIRST_ROLE_DESCRIPTION);
    final RoleData secondData = RoleData.from(SECOND_ROLE_ID, SECOND_ROLE_NAME, SECOND_ROLE_DESCRIPTION);
    registerExampleRole(firstData.toRoleState(), secondData.toRoleState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createUserAssignedToRole(firstData.toRoleState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, RoleData.class, interest);
    RoleData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_ROLE_ID.idString(), item.id);
    assertEquals(FIRST_ROLE_ID.tenantId, item.tenantId);
    assertEquals(FIRST_ROLE_NAME, item.name);
    assertEquals(FIRST_ROLE_DESCRIPTION, item.description);
  }

  @Test
  public void unassignUser() {
    final RoleData firstData = RoleData.from(FIRST_ROLE_ID, FIRST_ROLE_NAME, FIRST_ROLE_DESCRIPTION);
    final RoleData secondData = RoleData.from(SECOND_ROLE_ID, SECOND_ROLE_NAME, SECOND_ROLE_DESCRIPTION);
    registerExampleRole(firstData.toRoleState(), secondData.toRoleState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createUserUnassignedFromRole(firstData.toRoleState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, RoleData.class, interest);
    RoleData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_ROLE_ID.idString(), item.id);
    assertEquals(FIRST_ROLE_ID.tenantId, item.tenantId);
    assertEquals(FIRST_ROLE_NAME, item.name);
    assertEquals(FIRST_ROLE_DESCRIPTION, item.description);
  }

  @Test
  public void attach() {
    final RoleData firstData = RoleData.from(FIRST_ROLE_ID, FIRST_ROLE_NAME, FIRST_ROLE_DESCRIPTION);
    final RoleData secondData = RoleData.from(SECOND_ROLE_ID, SECOND_ROLE_NAME, SECOND_ROLE_DESCRIPTION);
    registerExampleRole(firstData.toRoleState(), secondData.toRoleState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createRolePermissionAttached(firstData.toRoleState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, RoleData.class, interest);
    RoleData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_ROLE_ID.idString(), item.id);
    assertEquals(FIRST_ROLE_ID.tenantId, item.tenantId);
    assertEquals(FIRST_ROLE_NAME, item.name);
    assertEquals(FIRST_ROLE_DESCRIPTION, item.description);
  }

  @Test
  public void detach() {
    final RoleData firstData = RoleData.from(FIRST_ROLE_ID, FIRST_ROLE_NAME, FIRST_ROLE_DESCRIPTION);
    final RoleData secondData = RoleData.from(SECOND_ROLE_ID, SECOND_ROLE_NAME, SECOND_ROLE_DESCRIPTION);
    registerExampleRole(firstData.toRoleState(), secondData.toRoleState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createRolePermissionDetached(firstData.toRoleState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, RoleData.class, interest);
    RoleData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_ROLE_ID.idString(), item.id);
    assertEquals(FIRST_ROLE_ID.tenantId, item.tenantId);
    assertEquals(FIRST_ROLE_NAME, item.name);
    assertEquals(FIRST_ROLE_DESCRIPTION, item.description);
  }

  private int valueOfProjectionIdFor(final String valueText, final Map<String, Integer> confirmations) {
    return confirmations.get(valueToProjectionId.get(valueText));
  }

  private Projectable createRoleProvisioned(RoleState data) {
    final RoleProvisioned eventData = new RoleProvisioned(data.roleId, data.name, data.description);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(RoleProvisioned.class, 1, JsonSerialization.serialized(eventData), 1, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.roleId.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createRoleDescriptionChanged(RoleState data) {
    final RoleDescriptionChanged eventData = new RoleDescriptionChanged(data.roleId, data.description);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(RoleDescriptionChanged.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.roleId.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createGroupAssignedToRole(RoleState data) {
    final GroupAssignedToRole eventData = new GroupAssignedToRole(data.roleId, data.name);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(GroupAssignedToRole.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.roleId.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createGroupUnassignedFromRole(RoleState data) {
    final GroupUnassignedFromRole eventData = new GroupUnassignedFromRole(data.roleId, data.name);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(GroupUnassignedFromRole.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.roleId.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createUserAssignedToRole(RoleState data) {
    final UserAssignedToRole eventData = new UserAssignedToRole(data.roleId, data.name);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserAssignedToRole.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.roleId.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createUserUnassignedFromRole(RoleState data) {
    final UserUnassignedFromRole eventData = new UserUnassignedFromRole(data.roleId, data.name);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserUnassignedFromRole.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.roleId.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createRolePermissionAttached(RoleState data) {
    final RolePermissionAttached eventData = new RolePermissionAttached(data.roleId, data.name);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(RolePermissionAttached.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.roleId.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createRolePermissionDetached(RoleState data) {
    final RolePermissionDetached eventData = new RolePermissionDetached(data.roleId, data.name);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(RolePermissionDetached.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.roleId.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

}
