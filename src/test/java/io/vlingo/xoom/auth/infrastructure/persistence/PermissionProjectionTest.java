package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.ConstraintData;
import io.vlingo.xoom.auth.infrastructure.PermissionData;
import io.vlingo.xoom.auth.model.permission.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
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
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PermissionProjectionTest {

  private final TenantId TENANT_ID = TenantId.from("b5744809-91c2-49d6-87fb-3221b8846ca0");
  private final String FIRST_PERMISSION_NAME = "permission-a";
  private final PermissionId FIRST_PERMISSION_ID = PermissionId.from(TENANT_ID, FIRST_PERMISSION_NAME);
  private final String SECOND_PERMISSION_NAME = "permission-b";
  private final PermissionId SECOND_PERMISSION_ID = PermissionId.from(TENANT_ID, SECOND_PERMISSION_NAME);

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
    StatefulTypeRegistry statefulTypeRegistry = StatefulTypeRegistry.registerAll(world, stateStore, PermissionData.class);
    QueryModelStateStoreProvider.using(world.stage(), statefulTypeRegistry);
    projection = world.actorFor(Projection.class, PermissionProjectionActor.class, stateStore);
  }

  private void registerExamplePermission(PermissionState firstData, PermissionState secondData) {
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(2);
    projection.projectWith(createPermissionProvisioned(firstData), control);
    projection.projectWith(createPermissionProvisioned(secondData), control);
  }

  @Test
  public void provisionPermission() {
    final ConstraintData firstConstraintData = ConstraintData.from("String", "constraint-A", "1", "Constraint A");
    final ConstraintData secondConstraintData = ConstraintData.from("String", "constraint-B", "1", "Constraint B");
    final PermissionData firstData = PermissionData.from(FIRST_PERMISSION_ID, new HashSet<>(Collections.singletonList(firstConstraintData)), FIRST_PERMISSION_NAME, "first-permission-description");
    final PermissionData secondData = PermissionData.from(SECOND_PERMISSION_ID, new HashSet<>(Collections.singletonList(secondConstraintData)), SECOND_PERMISSION_NAME, "second-permission-description");

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(2);
    projection.projectWith(createPermissionProvisioned(firstData.toPermissionState()), control);
    projection.projectWith(createPermissionProvisioned(secondData.toPermissionState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(2, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));
    assertEquals(1, valueOfProjectionIdFor(secondData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, PermissionData.class, interest);
    PermissionData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_PERMISSION_ID.idString(), item.id);
    assertEquals(FIRST_PERMISSION_NAME, item.name);
    assertEquals("first-permission-description", item.description);
    assertEquals(TENANT_ID.id, item.tenantId);

    interest = new CountingReadResultInterest();
    interestAccess = interest.afterCompleting(1);
    stateStore.read(secondData.id, PermissionData.class, interest);
    item = interestAccess.readFrom("item", secondData.id);
    assertEquals(secondData.id, item.id);
    assertEquals(SECOND_PERMISSION_ID.idString(), item.id);
    assertEquals(SECOND_PERMISSION_NAME, item.name);
    assertEquals("second-permission-description", item.description);
    assertEquals(TENANT_ID.id, item.tenantId);
  }

  @Test
  public void enforce() {
    final ConstraintData firstConstraintData = ConstraintData.from("String", "constraint-A", "1", "Constraint A");
    final ConstraintData secondConstraintData = ConstraintData.from("String", "constraint-B", "1", "Constraint B");
    final PermissionData firstData = PermissionData.from(FIRST_PERMISSION_ID, new HashSet<>(Collections.singletonList(firstConstraintData)), FIRST_PERMISSION_NAME, "first-permission-description");
    final PermissionData secondData = PermissionData.from(SECOND_PERMISSION_ID, new HashSet<>(Collections.singletonList(secondConstraintData)), SECOND_PERMISSION_NAME, "second-permission-description");
    registerExamplePermission(firstData.toPermissionState(), secondData.toPermissionState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createPermissionConstraintEnforced(firstData.toPermissionState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, PermissionData.class, interest);
    PermissionData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_PERMISSION_ID.idString(), item.id);
    assertNotNull(item.constraints);
  }

  @Test
  public void enforceReplacement() {
    final ConstraintData firstConstraintData = ConstraintData.from("String", "constraint-A", "1", "Constraint A");
    final ConstraintData secondConstraintData = ConstraintData.from("String", "constraint-B", "1", "Constraint B");
    final PermissionData firstData = PermissionData.from(FIRST_PERMISSION_ID, new HashSet<>(Collections.singletonList(firstConstraintData)), FIRST_PERMISSION_NAME, "first-permission-description");
    final PermissionData secondData = PermissionData.from(SECOND_PERMISSION_ID, new HashSet<>(Collections.singletonList(secondConstraintData)), SECOND_PERMISSION_NAME, "second-permission-description");
    registerExamplePermission(firstData.toPermissionState(), secondData.toPermissionState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createPermissionConstraintReplacementEnforced(firstData.toPermissionState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, PermissionData.class, interest);
    PermissionData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_PERMISSION_ID.idString(), item.id);
    assertNotNull(item.constraints);
  }

  @Test
  public void forget() {
    final ConstraintData firstConstraintData = ConstraintData.from("String", "constraint-A", "1", "Constraint A");
    final ConstraintData secondConstraintData = ConstraintData.from("String", "constraint-B", "1", "Constraint B");
    final PermissionData firstData = PermissionData.from(FIRST_PERMISSION_ID, new HashSet<>(Collections.singletonList(firstConstraintData)), FIRST_PERMISSION_NAME, "first-permission-description");
    final PermissionData secondData = PermissionData.from(SECOND_PERMISSION_ID, new HashSet<>(Collections.singletonList(secondConstraintData)), SECOND_PERMISSION_NAME, "second-permission-description");
    registerExamplePermission(firstData.toPermissionState(), secondData.toPermissionState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createPermissionConstraintForgotten(firstData.toPermissionState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, PermissionData.class, interest);
    PermissionData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_PERMISSION_ID.idString(), item.id);
    assertNotNull(item.constraints);
  }

  @Test
  public void changeDescription() {
    final ConstraintData firstConstraintData = ConstraintData.from("String", "constraint-A", "1", "Constraint A");
    final ConstraintData secondConstraintData = ConstraintData.from("String", "constraint-B", "1", "Constraint B");
    final PermissionData firstData = PermissionData.from(FIRST_PERMISSION_ID, new HashSet<>(Collections.singletonList(firstConstraintData)), FIRST_PERMISSION_NAME, "first-permission-description");
    final PermissionData secondData = PermissionData.from(SECOND_PERMISSION_ID, new HashSet<>(Collections.singletonList(secondConstraintData)), SECOND_PERMISSION_NAME, "second-permission-description");
    registerExamplePermission(firstData.toPermissionState(), secondData.toPermissionState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createPermissionDescriptionChanged(firstData.toPermissionState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, PermissionData.class, interest);
    PermissionData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(FIRST_PERMISSION_ID.idString(), item.id);
    assertEquals("first-permission-description", item.description);
    assertEquals(TENANT_ID.id, item.tenantId);
  }

  private int valueOfProjectionIdFor(final String valueText, final Map<String, Integer> confirmations) {
    final String key = valueToProjectionId.get(valueText);
    return confirmations.get(key);
  }

  private Projectable createPermissionProvisioned(PermissionState data) {
    final PermissionProvisioned eventData = new PermissionProvisioned(data.id, data.name, data.description);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(PermissionProvisioned.class, 1, JsonSerialization.serialized(eventData), 1, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createPermissionConstraintEnforced(PermissionState data) {
    final PermissionConstraintEnforced eventData = new PermissionConstraintEnforced(data.id, data.constraints.stream().findFirst().orElse(null));

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(PermissionConstraintEnforced.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createPermissionConstraintReplacementEnforced(PermissionState data) {
    final PermissionConstraintReplacementEnforced eventData = new PermissionConstraintReplacementEnforced(data.id, data.constraints.stream().findFirst().get().name, data.constraints.stream().findFirst().orElse(null));

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(PermissionConstraintReplacementEnforced.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createPermissionConstraintForgotten(PermissionState data) {
    final PermissionConstraintForgotten eventData = new PermissionConstraintForgotten(data.id, data.constraints.stream().findFirst().orElse(null));

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(PermissionConstraintForgotten.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createPermissionDescriptionChanged(PermissionState data) {
    final PermissionDescriptionChanged eventData = new PermissionDescriptionChanged(data.id, data.description);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(PermissionDescriptionChanged.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id.idString(), projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

}
