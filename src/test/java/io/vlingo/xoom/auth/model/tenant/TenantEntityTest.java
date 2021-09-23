package io.vlingo.xoom.auth.model.tenant;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.auth.infrastructure.persistence.TenantNameChangedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.TenantDescriptionChangedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.TenantSubscribedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.TenantDeactivatedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.TenantActivatedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.MockDispatcher;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.*;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class TenantEntityTest {

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;
  private Tenant tenant;

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

    tenant = world.actorFor(Tenant.class, TenantEntity.class, "#1");
  }

  private static final String NAME_FOR_SUBSCRIBE_FOR_TEST = "tenant-name";
  private static final String DESCRIPTION_FOR_SUBSCRIBE_FOR_TEST = "tenant-description";
  private static final boolean ACTIVE_FOR_SUBSCRIBE_FOR_TEST = true;

  @Test
  public void subscribeFor() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final TenantState state = tenant.subscribeFor(NAME_FOR_SUBSCRIBE_FOR_TEST, DESCRIPTION_FOR_SUBSCRIBE_FOR_TEST, ACTIVE_FOR_SUBSCRIBE_FOR_TEST).await();

    assertEquals(state.name, "tenant-name");
    assertEquals(state.description, "tenant-description");
    assertEquals(state.active, true);
    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(TenantSubscribed.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }


  @Test
  public void activate() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    tenant.activate().await();

    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(TenantActivated.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }


  @Test
  public void deactivate() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    tenant.deactivate().await();

    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(TenantDeactivated.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }

  private static final String NAME_FOR_CHANGE_NAME_TEST = "updated-tenant-name";

  @Test
  public void changeName() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final TenantState state = tenant.changeName(NAME_FOR_CHANGE_NAME_TEST).await();

    assertEquals(state.description, "tenant-description");
    assertEquals(state.active, true);
    assertEquals(state.name, "updated-tenant-name");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(TenantNameChanged.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String DESCRIPTION_FOR_CHANGE_DESCRIPTION_TEST = "updated-tenant-description";

  @Test
  public void changeDescription() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final TenantState state = tenant.changeDescription(DESCRIPTION_FOR_CHANGE_DESCRIPTION_TEST).await();

    assertEquals(state.name, "tenant-name");
    assertEquals(state.active, true);
    assertEquals(state.description, "updated-tenant-description");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(TenantDescriptionChanged.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String NAME_FOR_ENTITY_CREATION = "tenant-name";
  private static final String DESCRIPTION_FOR_ENTITY_CREATION = "tenant-description";
  private static final boolean ACTIVE_FOR_ENTITY_CREATION = true;

  private void _createEntity() {
    tenant.subscribeFor(NAME_FOR_ENTITY_CREATION, DESCRIPTION_FOR_ENTITY_CREATION, ACTIVE_FOR_ENTITY_CREATION).await();
  }
}
