package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.auth.infrastructure.persistence.UserCredentialReplacedAdapter;
import java.util.*;
import io.vlingo.xoom.auth.infrastructure.persistence.UserRegisteredAdapter;
import io.vlingo.xoom.auth.model.value.*;
import io.vlingo.xoom.auth.infrastructure.persistence.UserCredentialAddedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.UserCredentialRemovedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.UserProfileReplacedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.UserDeactivatedAdapter;
import io.vlingo.xoom.auth.infrastructure.persistence.MockDispatcher;
import io.vlingo.xoom.auth.infrastructure.persistence.UserActivatedAdapter;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.*;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntityTest {

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;
  private User user;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp(){
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);

    entryAdapterProvider.registerAdapter(UserRegistered.class, new UserRegisteredAdapter());
    entryAdapterProvider.registerAdapter(UserActivated.class, new UserActivatedAdapter());
    entryAdapterProvider.registerAdapter(UserDeactivated.class, new UserDeactivatedAdapter());
    entryAdapterProvider.registerAdapter(UserCredentialAdded.class, new UserCredentialAddedAdapter());
    entryAdapterProvider.registerAdapter(UserCredentialRemoved.class, new UserCredentialRemovedAdapter());
    entryAdapterProvider.registerAdapter(UserCredentialReplaced.class, new UserCredentialReplacedAdapter());
    entryAdapterProvider.registerAdapter(UserProfileReplaced.class, new UserProfileReplacedAdapter());

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, UserEntity.class, UserEntity.class.getSimpleName()));

    user = world.actorFor(User.class, UserEntity.class, "#1");
  }

  private static final String TENANT_ID_FOR_REGISTER_USER_TEST = "user-tenantId";
  private static final String USERNAME_FOR_REGISTER_USER_TEST = "user-username";
  private static final boolean ACTIVE_FOR_REGISTER_USER_TEST = true;
  private static final Profile PROFILE_FOR_REGISTER_USER_TEST = Profile.from("user-profile-emailAddress", PersonName.from("user-profile-name-given", "user-profile-name-family", "user-profile-name-second"), "user-profile-phone");

  @Test
  public void registerUser() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final UserState state = user.registerUser(TENANT_ID_FOR_REGISTER_USER_TEST, USERNAME_FOR_REGISTER_USER_TEST, ACTIVE_FOR_REGISTER_USER_TEST, PROFILE_FOR_REGISTER_USER_TEST).await();

    assertEquals(state.tenantId, "user-tenantId");
    assertEquals(state.username, "user-username");
    assertEquals(state.active, true);
    assertEquals(state.profile.emailAddress, "user-profile-emailAddress");
    assertEquals(state.profile.name.given, "user-profile-name-given");
    assertEquals(state.profile.name.family, "user-profile-name-family");
    assertEquals(state.profile.name.second, "user-profile-name-second");
    assertEquals(state.profile.phone, "user-profile-phone");
    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserRegistered.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }

  private static final String TENANT_ID_FOR_ACTIVATE_TEST = "updated-user-tenantId";
  private static final String USERNAME_FOR_ACTIVATE_TEST = "updated-user-username";

  @Test
  public void activate() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final UserState state = user.activate(TENANT_ID_FOR_ACTIVATE_TEST, USERNAME_FOR_ACTIVATE_TEST).await();

    assertEquals(state.active, true);
    assertEquals(state.profile.emailAddress, "user-profile-emailAddress");
    assertEquals(state.profile.name.given, "user-profile-name-given");
    assertEquals(state.profile.name.family, "user-profile-name-family");
    assertEquals(state.profile.name.second, "user-profile-name-second");
    assertEquals(state.profile.phone, "user-profile-phone");
    assertEquals(state.tenantId, "updated-user-tenantId");
    assertEquals(state.username, "updated-user-username");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserActivated.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_DEACTIVATE_TEST = "updated-user-tenantId";
  private static final String USERNAME_FOR_DEACTIVATE_TEST = "updated-user-username";

  @Test
  public void deactivate() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final UserState state = user.deactivate(TENANT_ID_FOR_DEACTIVATE_TEST, USERNAME_FOR_DEACTIVATE_TEST).await();

    assertEquals(state.active, true);
    assertEquals(state.profile.emailAddress, "user-profile-emailAddress");
    assertEquals(state.profile.name.given, "user-profile-name-given");
    assertEquals(state.profile.name.family, "user-profile-name-family");
    assertEquals(state.profile.name.second, "user-profile-name-second");
    assertEquals(state.profile.phone, "user-profile-phone");
    assertEquals(state.tenantId, "updated-user-tenantId");
    assertEquals(state.username, "updated-user-username");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserDeactivated.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final Credential CREDENTIALS_FOR_ADD_CREDENTIAL_TEST = Credential.from("updated-user-credentials-authority", "updated-1", "updated-user-credentials-secret", "updated-user-credentials-type");

  @Test
  public void addCredential() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final UserState state = user.addCredential(CREDENTIALS_FOR_ADD_CREDENTIAL_TEST).await();

    assertEquals(state.tenantId, "user-tenantId");
    assertEquals(state.username, "user-username");
    assertEquals(state.active, true);
    assertEquals(state.profile.emailAddress, "user-profile-emailAddress");
    assertEquals(state.profile.name.given, "user-profile-name-given");
    assertEquals(state.profile.name.family, "user-profile-name-family");
    assertEquals(state.profile.name.second, "user-profile-name-second");
    assertEquals(state.profile.phone, "user-profile-phone");
    assertNotNull(state.credentials);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserCredentialAdded.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final Credential CREDENTIALS_FOR_REMOVE_CREDENTIAL_TEST = Credential.from("updated-user-credentials-authority", "updated-1", "updated-user-credentials-secret", "updated-user-credentials-type");

  @Test
  public void removeCredential() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final UserState state = user.removeCredential(CREDENTIALS_FOR_REMOVE_CREDENTIAL_TEST).await();

    assertEquals(state.tenantId, "user-tenantId");
    assertEquals(state.username, "user-username");
    assertEquals(state.active, true);
    assertEquals(state.profile.emailAddress, "user-profile-emailAddress");
    assertEquals(state.profile.name.given, "user-profile-name-given");
    assertEquals(state.profile.name.family, "user-profile-name-family");
    assertEquals(state.profile.name.second, "user-profile-name-second");
    assertEquals(state.profile.phone, "user-profile-phone");
    assertNotNull(state.credentials);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserCredentialRemoved.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final Credential CREDENTIALS_FOR_REPLACE_CREDENTIAL_TEST = Credential.from("updated-user-credentials-authority", "updated-1", "updated-user-credentials-secret", "updated-user-credentials-type");

  @Test
  public void replaceCredential() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final UserState state = user.replaceCredential(CREDENTIALS_FOR_REPLACE_CREDENTIAL_TEST).await();

    assertEquals(state.tenantId, "user-tenantId");
    assertEquals(state.username, "user-username");
    assertEquals(state.active, true);
    assertEquals(state.profile.emailAddress, "user-profile-emailAddress");
    assertEquals(state.profile.name.given, "user-profile-name-given");
    assertEquals(state.profile.name.family, "user-profile-name-family");
    assertEquals(state.profile.name.second, "user-profile-name-second");
    assertEquals(state.profile.phone, "user-profile-phone");
    assertNotNull(state.credentials);
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserCredentialReplaced.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_REPLACE_PROFILE_TEST = "updated-user-tenantId";
  private static final String USERNAME_FOR_REPLACE_PROFILE_TEST = "updated-user-username";
  private static final Profile PROFILE_FOR_REPLACE_PROFILE_TEST = Profile.from("updated-user-profile-emailAddress", PersonName.from("updated-user-profile-name-given", "updated-user-profile-name-family", "updated-user-profile-name-second"), "updated-user-profile-phone");

  @Test
  public void replaceProfile() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final UserState state = user.replaceProfile(TENANT_ID_FOR_REPLACE_PROFILE_TEST, USERNAME_FOR_REPLACE_PROFILE_TEST, PROFILE_FOR_REPLACE_PROFILE_TEST).await();

    assertEquals(state.active, true);
    assertEquals(state.tenantId, "updated-user-tenantId");
    assertEquals(state.username, "updated-user-username");
    assertEquals(state.profile.emailAddress, "updated-user-profile-emailAddress");
    assertEquals(state.profile.name.given, "updated-user-profile-name-given");
    assertEquals(state.profile.name.family, "updated-user-profile-name-family");
    assertEquals(state.profile.name.second, "updated-user-profile-name-second");
    assertEquals(state.profile.phone, "updated-user-profile-phone");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(UserProfileReplaced.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final String TENANT_ID_FOR_ENTITY_CREATION = "user-tenantId";
  private static final String USERNAME_FOR_ENTITY_CREATION = "user-username";
  private static final boolean ACTIVE_FOR_ENTITY_CREATION = true;
  private static final Profile PROFILE_FOR_ENTITY_CREATION = Profile.from("user-profile-emailAddress", PersonName.from("user-profile-name-given", "user-profile-name-family", "user-profile-name-second"), "user-profile-phone");

  private void _createEntity() {
    user.registerUser(TENANT_ID_FOR_ENTITY_CREATION, USERNAME_FOR_ENTITY_CREATION, ACTIVE_FOR_ENTITY_CREATION, PROFILE_FOR_ENTITY_CREATION).await();
  }
}
