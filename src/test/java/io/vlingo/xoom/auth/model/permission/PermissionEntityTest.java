package io.vlingo.xoom.auth.model.permission;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionConstraintEnforcedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionProvisionedAdapter;
import io.vlingo.xoom.symbio.BaseEntry;
import java.util.*;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionConstraintForgottenAdapter;
import io.vlingo.xoom.auth.model.value.*;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionDescriptionChangedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionConstraintReplacementEnforcedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.MockDispatcher;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.*;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class PermissionEntityTest {

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;
  private Permission permission;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp(){
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);

    entryAdapterProvider.registerAdapter(PermissionProvisioned.class, new PermissionProvisionedAdapter());
    entryAdapterProvider.registerAdapter(PermissionConstraintEnforced.class, new PermissionConstraintEnforcedAdapter());
    entryAdapterProvider.registerAdapter(PermissionConstraintReplacementEnforced.class, new PermissionConstraintReplacementEnforcedAdapter());
    entryAdapterProvider.registerAdapter(PermissionConstraintForgotten.class, new PermissionConstraintForgottenAdapter());
    entryAdapterProvider.registerAdapter(PermissionDescriptionChanged.class, new PermissionDescriptionChangedAdapter());

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, PermissionEntity.class, PermissionEntity.class.getSimpleName()));

    permission = world.actorFor(Permission.class, PermissionEntity.class, "#1");
  }

  private static final String DESCRIPTION_FOR_PROVISION_PERMISSION_TEST = "permission-description";
  private static final String NAME_FOR_PROVISION_PERMISSION_TEST = "permission-name";
  private static final String TENANT_ID_FOR_PROVISION_PERMISSION_TEST = "permission-tenantId";

  @Test
  public void provisionPermission() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final PermissionState state = permission.provisionPermission(DESCRIPTION_FOR_PROVISION_PERMISSION_TEST, NAME_FOR_PROVISION_PERMISSION_TEST, TENANT_ID_FOR_PROVISION_PERMISSION_TEST).await();

    assertEquals(state.description, "permission-description");
    assertEquals(state.name, "permission-name");
    assertEquals(state.tenantId, "permission-tenantId");
    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(PermissionProvisioned.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }

  private static final Constraint CONSTRAINTS_FOR_ENFORCE_TEST = Constraint.from("updated-permission-constraints-description", "updated-permission-constraints-name", "updated-permission-constraints-type", "updated-permission-constraints-value");

  @Test
  public void enforce() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final PermissionState state = permission.enforce(CONSTRAINTS_FOR_ENFORCE_TEST).await();

    assertEquals(state.description, "permission-description");
    assertEquals(state.name, "permission-name");
    assertEquals(state.tenantId, "permission-tenantId");
    assertNotNull(state.constraints);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(PermissionConstraintEnforced.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final Constraint CONSTRAINTS_FOR_ENFORCE_REPLACEMENT_TEST = Constraint.from("updated-permission-constraints-description", "updated-permission-constraints-name", "updated-permission-constraints-type", "updated-permission-constraints-value");

  @Test
  public void enforceReplacement() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final PermissionState state = permission.enforceReplacement(CONSTRAINTS_FOR_ENFORCE_REPLACEMENT_TEST).await();

    assertEquals(state.description, "permission-description");
    assertEquals(state.name, "permission-name");
    assertEquals(state.tenantId, "permission-tenantId");
    assertNotNull(state.constraints);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(PermissionConstraintReplacementEnforced.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final Constraint CONSTRAINTS_FOR_FORGET_TEST = Constraint.from("updated-permission-constraints-description", "updated-permission-constraints-name", "updated-permission-constraints-type", "updated-permission-constraints-value");

  @Test
  public void forget() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final PermissionState state = permission.forget(CONSTRAINTS_FOR_FORGET_TEST).await();

    assertEquals(state.description, "permission-description");
    assertEquals(state.name, "permission-name");
    assertEquals(state.tenantId, "permission-tenantId");
    assertNotNull(state.constraints);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(PermissionConstraintForgotten.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String DESCRIPTION_FOR_CHANGE_DESCRIPTION_TEST = "updated-permission-description";
  private static final String TENANT_ID_FOR_CHANGE_DESCRIPTION_TEST = "updated-permission-tenantId";

  @Test
  public void changeDescription() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final PermissionState state = permission.changeDescription(DESCRIPTION_FOR_CHANGE_DESCRIPTION_TEST, TENANT_ID_FOR_CHANGE_DESCRIPTION_TEST).await();

    assertEquals(state.name, "permission-name");
    assertEquals(state.description, "updated-permission-description");
    assertEquals(state.tenantId, "updated-permission-tenantId");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(PermissionDescriptionChanged.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String DESCRIPTION_FOR_ENTITY_CREATION = "permission-description";
  private static final String NAME_FOR_ENTITY_CREATION = "permission-name";
  private static final String TENANT_ID_FOR_ENTITY_CREATION = "permission-tenantId";

  private void _createEntity() {
    permission.provisionPermission(DESCRIPTION_FOR_ENTITY_CREATION, NAME_FOR_ENTITY_CREATION, TENANT_ID_FOR_ENTITY_CREATION).await();
  }
}
