package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.Configuration;
import io.vlingo.xoom.actors.UUIDAddressFactory;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.actors.testkit.TestWorld;
import io.vlingo.xoom.common.identity.IdentityGeneratorType;
import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;
import io.vlingo.xoom.lattice.model.projection.Projection;
import io.vlingo.xoom.lattice.model.projection.TextProjectable;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class ProjectionTest {
  protected World world;
  protected TestWorld testWorld;
  protected StateStore stateStore;
  protected Projection projection;

  @BeforeEach
  public void setUp() throws Exception {
    testWorld = TestWorld.start("test-projection", Configuration.define().with(new UUIDAddressFactory(IdentityGeneratorType.RANDOM)));
    world = testWorld.world();
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry statefulTypeRegistry = StatefulTypeRegistry.registerAll(world, stateStore, statefulTypes().toArray(new Class[0]));
    QueryModelStateStoreProvider.using(world.stage(), statefulTypeRegistry);
    projection = projection();
  }

  @AfterEach
  public void tearDown() {
    testWorld.terminate();
  }

  abstract protected Set<Class<?>> statefulTypes();

  abstract protected Projection projection();

  protected void givenEvents(IdentifiedDomainEvent... events) {
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(events.length);
    final Map<String, String> valueToProjectionId = new HashMap<>();
    final Map<String, Integer> entryVersions = new HashMap<>();

    Arrays.stream(events).forEach(event -> {
      Integer entryVersion = entryVersions.getOrDefault(event.identity(), 0) + 1;
      entryVersions.put(event.identity(), entryVersion);
      BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(event.getClass(), 1, JsonSerialization.serialized(event), entryVersion, Metadata.withObject(event));

      final String projectionId = UUID.randomUUID().toString();
      valueToProjectionId.put(event.identity(), projectionId);

      projection.projectWith(new TextProjectable(null, Collections.singletonList(textEntry), projectionId), control);
    });

    final Map<String, Integer> confirmations = access.readFrom("confirmations");
    assertEquals(events.length, confirmations.size());
    Arrays.stream(events).forEach(event -> assertEquals(1, confirmations.get(valueToProjectionId.get(event.identity()))));
  }
}