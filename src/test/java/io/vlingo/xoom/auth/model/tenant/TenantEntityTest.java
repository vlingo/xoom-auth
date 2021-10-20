package io.vlingo.xoom.auth.model.tenant;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static io.vlingo.xoom.auth.test.Assertions.assertEventDispatched;
import static org.junit.jupiter.api.Assertions.*;

public class TenantEntityTest {

  private static final TenantId TENANT_ID = TenantId.from("05e66197-b1c8-4fe4-bf75-25855ec06fa5");
  private static final String TENANT_NAME = "tenant-name";
  private static final String TENANT_DESCRIPTION = "tenant-description";

  private World world;
  private MockDispatcher dispatcher;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp(){
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    final EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);
    entryAdapterProvider.registerAdapter(TenantActivated.class, new TenantActivatedAdapter());
    entryAdapterProvider.registerAdapter(TenantDeactivated.class, new TenantDeactivatedAdapter());
    entryAdapterProvider.registerAdapter(TenantDescriptionChanged.class, new TenantDescriptionChangedAdapter());
    entryAdapterProvider.registerAdapter(TenantNameChanged.class, new TenantNameChangedAdapter());
    entryAdapterProvider.registerAdapter(TenantSubscribed.class, new TenantSubscribedAdapter());

    final Journal<String> journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, TenantEntity.class, TenantEntity.class.getSimpleName()));
  }

  @Test
  public void tenantSubscribesWithNameAndDescription() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Completes<TenantState> outcome = tenantOf(TENANT_ID)
            .subscribeFor(TENANT_NAME, TENANT_DESCRIPTION, true);

    assertCompletes(outcome, state -> {
      assertEquals(TENANT_NAME, state.name);
      assertEquals(TENANT_DESCRIPTION, state.description);
      assertTrue(state.active);
      assertEventDispatched(dispatcherAccess, 1, TenantSubscribed.class);
    });
  }

  @Test
  public void tenantIsActivated() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<TenantState> outcome = givenInactiveTenant(TENANT_ID)
            .andThenTo(t -> tenantOf(TENANT_ID).activate());

    assertCompletes(outcome, state -> {
      assertTrue(state.active);
      assertEventDispatched(dispatcherAccess, 2, TenantActivated.class);
    });
  }

  @Test
  public void tenantIsDeactivated() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<TenantState> outcome = givenActiveTenant(TENANT_ID)
            .andThenTo(t -> tenantOf(TENANT_ID).deactivate());

    assertCompletes(outcome, state -> {
      assertFalse(state.active);
      assertEventDispatched(dispatcherAccess, 2, TenantDeactivated.class);
    });
  }

  @Test
  public void tenantNameIsChanged() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<TenantState> outcome = givenActiveTenant(TENANT_ID)
            .andThenTo(t -> tenantOf(TENANT_ID).changeName("updated-tenant-name"));

    assertCompletes(outcome, state -> {
      assertEquals("updated-tenant-name", state.name);
      assertEventDispatched(dispatcherAccess, 2, TenantNameChanged.class);
    });
  }

  @Test
  public void tenantDescriptionIsChanged() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<TenantState> outcome = givenActiveTenant(TENANT_ID)
            .andThenTo(t -> tenantOf(TENANT_ID).changeDescription("updated-tenant-description"));

    assertCompletes(outcome, state -> {
      assertEquals("updated-tenant-description", state.description);
      assertEventDispatched(dispatcherAccess, 2, TenantDescriptionChanged.class);
    });
  }

  private Completes<TenantState> givenActiveTenant(final TenantId tenantId) {
    return tenantOf(tenantId).subscribeFor(TENANT_NAME, TENANT_DESCRIPTION, true);
  }

  private Completes<TenantState> givenInactiveTenant(final TenantId tenantId) {
    return tenantOf(tenantId).subscribeFor(TENANT_NAME, TENANT_DESCRIPTION, false);
  }

  private Tenant tenantOf(final TenantId tenantId) {
    return world.actorFor(Tenant.class, TenantEntity.class, tenantId);
  }
}
