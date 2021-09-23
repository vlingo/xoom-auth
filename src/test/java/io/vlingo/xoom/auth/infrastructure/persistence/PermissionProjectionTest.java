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

import io.vlingo.xoom.auth.model.permission.*;
import java.util.*;
import io.vlingo.xoom.auth.infrastructure.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PermissionProjectionTest {

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

  private Projectable createPermissionProvisioned(PermissionState data) {
    final PermissionProvisioned eventData = new PermissionProvisioned(data.id, data.description, data.name, data.tenantId);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(PermissionProvisioned.class, 1,
    JsonSerialization.serialized(eventData), 1, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  @Test
  public void provisionPermission() {
    final PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    final PermissionData secondData = PermissionData.from("2", new HashSet<>(), "second-permission-description", "second-permission-name", "second-permission-tenantId");
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
    assertEquals(item.id, "1");
    assertEquals(item.description, "first-permission-description");
    assertEquals(item.name, "first-permission-name");
    assertEquals(item.tenantId, "first-permission-tenantId");

    interest = new CountingReadResultInterest();
    interestAccess = interest.afterCompleting(1);
    stateStore.read(secondData.id, PermissionData.class, interest);
    item = interestAccess.readFrom("item", secondData.id);
    assertEquals(secondData.id, item.id);
    assertEquals(item.id, "2");
    assertEquals(item.description, "second-permission-description");
    assertEquals(item.name, "second-permission-name");
    assertEquals(item.tenantId, "second-permission-tenantId");
  }

  private Projectable createPermissionConstraintEnforced(PermissionState data) {
    final PermissionConstraintEnforced eventData = new PermissionConstraintEnforced(data.id, data.constraints.stream().findFirst().orElse(null));
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(PermissionConstraintEnforced.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  @Test
  public void enforce() {
    final PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    final PermissionData secondData = PermissionData.from("2", new HashSet<>(), "second-permission-description", "second-permission-name", "second-permission-tenantId");
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
    assertEquals(item.id, "1");
    assertNotNull(item.constraints);
  }

  private Projectable createPermissionConstraintReplacementEnforced(PermissionState data) {
    final PermissionConstraintReplacementEnforced eventData = new PermissionConstraintReplacementEnforced(data.id, data.constraints.stream().findFirst().orElse(null));
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(PermissionConstraintReplacementEnforced.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  @Test
  public void enforceReplacement() {
    final PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    final PermissionData secondData = PermissionData.from("2", new HashSet<>(), "second-permission-description", "second-permission-name", "second-permission-tenantId");
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
    assertEquals(item.id, "1");
    assertNotNull(item.constraints);
  }

  private Projectable createPermissionConstraintForgotten(PermissionState data) {
    final PermissionConstraintForgotten eventData = new PermissionConstraintForgotten(data.id, data.constraints.stream().findFirst().orElse(null));
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(PermissionConstraintForgotten.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  @Test
  public void forget() {
    final PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    final PermissionData secondData = PermissionData.from("2", new HashSet<>(), "second-permission-description", "second-permission-name", "second-permission-tenantId");
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
    assertEquals(item.id, "1");
    assertNotNull(item.constraints);
  }

  private Projectable createPermissionDescriptionChanged(PermissionState data) {
    final PermissionDescriptionChanged eventData = new PermissionDescriptionChanged(data.id, data.description, data.tenantId);
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(PermissionDescriptionChanged.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  @Test
  public void changeDescription() {
    final PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    final PermissionData secondData = PermissionData.from("2", new HashSet<>(), "second-permission-description", "second-permission-name", "second-permission-tenantId");
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
    assertEquals(item.id, "1");
    assertEquals(item.description, "first-permission-description");
    assertEquals(item.tenantId, "first-permission-tenantId");
  }

  private int valueOfProjectionIdFor(final String valueText, final Map<String, Integer> confirmations) {
    return confirmations.get(valueToProjectionId.get(valueText));
  }
}
