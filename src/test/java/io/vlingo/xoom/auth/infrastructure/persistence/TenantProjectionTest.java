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

import io.vlingo.xoom.auth.model.tenant.*;
import io.vlingo.xoom.auth.infrastructure.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TenantProjectionTest {

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
    StatefulTypeRegistry statefulTypeRegistry = StatefulTypeRegistry.registerAll(world, stateStore, TenantData.class);
    QueryModelStateStoreProvider.using(world.stage(), statefulTypeRegistry);
    projection = world.actorFor(Projection.class, TenantProjectionActor.class, stateStore);
  }

  private void registerExampleTenant(TenantState firstData, TenantState secondData) {
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(2);
    projection.projectWith(createTenantSubscribed(firstData), control);
    projection.projectWith(createTenantSubscribed(secondData), control);
  }

  private Projectable createTenantSubscribed(TenantState data) {
    final TenantSubscribed eventData = new TenantSubscribed(data.id, data.name, data.description, data.active);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(TenantSubscribed.class, 1,
    JsonSerialization.serialized(eventData), 1, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  @Test
  public void subscribeFor() {
    final TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    final TenantData secondData = TenantData.from("2", "second-tenant-name", "second-tenant-description", true);
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(2);
    projection.projectWith(createTenantSubscribed(firstData.toTenantState()), control);
    projection.projectWith(createTenantSubscribed(secondData.toTenantState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(2, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));
    assertEquals(1, valueOfProjectionIdFor(secondData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, TenantData.class, interest);
    TenantData item = interestAccess.readFrom("item", firstData.id);
    assertEquals(item.id, "1");
    assertEquals(item.name, "first-tenant-name");
    assertEquals(item.description, "first-tenant-description");
    assertEquals(item.active, true);

    interest = new CountingReadResultInterest();
    interestAccess = interest.afterCompleting(1);
    stateStore.read(secondData.id, TenantData.class, interest);
    item = interestAccess.readFrom("item", secondData.id);
    assertEquals(secondData.id, item.id);
    assertEquals(item.id, "2");
    assertEquals(item.name, "second-tenant-name");
    assertEquals(item.description, "second-tenant-description");
    assertEquals(item.active, true);
  }

  private Projectable createTenantActivated(TenantState data) {
    final TenantActivated eventData = new TenantActivated(data.id);
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(TenantActivated.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  @Test
  public void activate() {
    final TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    final TenantData secondData = TenantData.from("2", "second-tenant-name", "second-tenant-description", true);
    registerExampleTenant(firstData.toTenantState(), secondData.toTenantState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createTenantActivated(firstData.toTenantState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, TenantData.class, interest);
    TenantData item = interestAccess.readFrom("item", firstData.id);
    assertEquals(item.id, "1");
  }

  private Projectable createTenantDeactivated(TenantState data) {
    final TenantDeactivated eventData = new TenantDeactivated(data.id);
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(TenantDeactivated.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  @Test
  public void deactivate() {
    final TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    final TenantData secondData = TenantData.from("2", "second-tenant-name", "second-tenant-description", true);
    registerExampleTenant(firstData.toTenantState(), secondData.toTenantState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createTenantDeactivated(firstData.toTenantState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, TenantData.class, interest);
    TenantData item = interestAccess.readFrom("item", firstData.id);
    assertEquals(item.id, "1");
  }

  private Projectable createTenantNameChanged(TenantState data) {
    final TenantNameChanged eventData = new TenantNameChanged(data.id, data.name);
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(TenantNameChanged.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  @Test
  public void changeName() {
    final TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    final TenantData secondData = TenantData.from("2", "second-tenant-name", "second-tenant-description", true);
    registerExampleTenant(firstData.toTenantState(), secondData.toTenantState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createTenantNameChanged(firstData.toTenantState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, TenantData.class, interest);
    TenantData item = interestAccess.readFrom("item", firstData.id);
    assertEquals(item.id, "1");
    assertEquals(item.name, "first-tenant-name");
  }

  private Projectable createTenantDescriptionChanged(TenantState data) {
    final TenantDescriptionChanged eventData = new TenantDescriptionChanged(data.id, data.description);
    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(TenantDescriptionChanged.class, 1,
    JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));
    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);
    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  @Test
  public void changeDescription() {
    final TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    final TenantData secondData = TenantData.from("2", "second-tenant-name", "second-tenant-description", true);
    registerExampleTenant(firstData.toTenantState(), secondData.toTenantState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createTenantDescriptionChanged(firstData.toTenantState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, TenantData.class, interest);
    TenantData item = interestAccess.readFrom("item", firstData.id);
    assertEquals(item.id, "1");
    assertEquals(item.description, "first-tenant-description");
  }

  private int valueOfProjectionIdFor(final String valueText, final Map<String, Integer> confirmations) {
    return confirmations.get(valueToProjectionId.get(valueText));
  }
}
