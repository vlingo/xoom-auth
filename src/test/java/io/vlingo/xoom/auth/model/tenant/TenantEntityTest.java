package io.vlingo.xoom.auth.model.tenant;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.vlingo.xoom.auth.test.Assertions.assertEventDispatched;
import static org.junit.jupiter.api.Assertions.*;

public class TenantEntityTest {

  private static final TenantId TENANT_ID = TenantId.from("05e66197-b1c8-4fe4-bf75-25855ec06fa5");
  private static final String TENANT_NAME = "tenant-name";
  private static final String TENANT_DESCRIPTION = "tenant-description";

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp(){
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);

    entryAdapterProvider.registerAdapter(TenantActivated.class, new TenantActivatedAdapter());
    entryAdapterProvider.registerAdapter(TenantDeactivated.class, new TenantDeactivatedAdapter());
    entryAdapterProvider.registerAdapter(TenantDescriptionChanged.class, new TenantDescriptionChangedAdapter());
    entryAdapterProvider.registerAdapter(TenantNameChanged.class, new TenantNameChangedAdapter());
    entryAdapterProvider.registerAdapter(TenantSubscribed.class, new TenantSubscribedAdapter());

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, TenantEntity.class, TenantEntity.class.getSimpleName()));
  }

  @Test
  public void subscribeFor() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final TenantState state = tenantOf(TENANT_ID).subscribeFor(TENANT_NAME, TENANT_DESCRIPTION, true).await();

    assertEquals(TENANT_NAME, state.name);
    assertEquals(TENANT_DESCRIPTION, state.description);
    assertTrue(state.active);
    assertEventDispatched(dispatcherAccess, 1, TenantSubscribed.class);
  }

  @Test
  public void activate() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenInactiveTenant(TENANT_ID);

    final TenantState state = tenantOf(TENANT_ID).activate().await();

    assertTrue(state.active);
    assertEventDispatched(dispatcherAccess, 2, TenantActivated.class);
  }

  @Test
  public void deactivate() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenActiveTenant(TENANT_ID);

    final TenantState state = tenantOf(TENANT_ID).deactivate().await();

    assertFalse(state.active);
    assertEventDispatched(dispatcherAccess, 2, TenantDeactivated.class);
  }

  @Test
  public void changeName() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenActiveTenant(TENANT_ID);

    final TenantState state = tenantOf(TENANT_ID).changeName("updated-tenant-name").await();

    assertEquals("updated-tenant-name", state.name);
    assertEventDispatched(dispatcherAccess, 2, TenantNameChanged.class);
  }

  @Test
  public void changeDescription() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenActiveTenant(TENANT_ID);

    final TenantState state = tenantOf(TENANT_ID).changeDescription("updated-tenant-description").await();

    assertEquals("updated-tenant-description", state.description);
    assertEventDispatched(dispatcherAccess, 2, TenantDescriptionChanged.class);
  }

  private void givenActiveTenant(TenantId tenantId) {
    tenantOf(TENANT_ID).subscribeFor(TENANT_NAME, TENANT_DESCRIPTION, true).await();
  }

  private void givenInactiveTenant(TenantId tenantId) {
    tenantOf(TENANT_ID).subscribeFor(TENANT_NAME, TENANT_DESCRIPTION, false).await();
  }

  private Tenant tenantOf(TenantId tenantId) {
    return world.actorFor(Tenant.class, TenantEntity.class, tenantId);
  }
}
