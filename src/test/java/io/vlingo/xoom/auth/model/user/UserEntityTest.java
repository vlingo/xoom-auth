package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.value.Credential;
import io.vlingo.xoom.auth.model.value.PersonName;
import io.vlingo.xoom.auth.model.value.Profile;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntityTest {

  private final TenantId TENANT_ID = TenantId.from("8b727090-652a-4b02-90fc-2e890d09a1c5");
  private final String USER_USERNAME = "bob";
  private final Profile USER_PROFILE = Profile.from("bob@example.com", PersonName.from("Bob", "Smith", "Cecil"), "07926123123");
  private final Credential USER_CREDENTIAL = Credential.from("user-credential-authority", "user-credential-id", "user-credential-secret", "user-credential-type");
  private final Set<Credential> USER_CREDENTIALS = Collections.singleton(USER_CREDENTIAL);
  private final UserId USER_ID = UserId.from(TENANT_ID, USER_USERNAME);

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;

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
  }

  @AfterEach
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void userIsRegistered() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final UserState state = userOf(USER_ID).registerUser(USER_USERNAME, USER_PROFILE, USER_CREDENTIALS, true).await();

    assertEquals(USER_ID, state.userId);
    assertEquals(USER_USERNAME, state.username);
    assertEquals(true, state.active);
    assertEquals(USER_PROFILE, state.profile);
    assertEquals(USER_CREDENTIALS, state.credentials);
    assertEventDispatched(dispatcherAccess, 1, UserRegistered.class);
  }

  @Test
  public void userIsActivated() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenInactiveUser(USER_ID);

    final UserState state = userOf(USER_ID).activate().await();

    assertTrue(state.active);
    assertEventDispatched(dispatcherAccess, 2, UserActivated.class);
  }

  @Test
  public void userIsDeactivated() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenActiveUser(USER_ID);

    final UserState state = userOf(USER_ID).deactivate().await();

    assertFalse(state.active);
    assertEventDispatched(dispatcherAccess, 2, UserDeactivated.class);
  }

  @Test
  public void credentialsAreAddedToRegisteredUser() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenActiveUser(USER_ID);

    final Credential newCredential = Credential.from("updated-user-credentials-authority", "updated-1", "updated-user-credentials-secret", "updated-user-credentials-type");
    final UserState state = userOf(USER_ID).addCredential(newCredential).await();

    assertEquals(USER_ID, state.userId);
    assertEquals(new HashSet<>(Arrays.asList(USER_CREDENTIAL, newCredential)), state.credentials);
    assertEventDispatched(dispatcherAccess, 2, UserCredentialAdded.class);
  }

  @Test
  public void credentialsAreRemovedFromRegisteredUser() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenActiveUser(USER_ID);

    final UserState state = userOf(USER_ID).removeCredential(USER_CREDENTIAL.authority).await();

    assertEquals(USER_ID, state.userId);
    assertEquals(0, state.credentials.size());
    assertEventDispatched(dispatcherAccess, 2, UserCredentialRemoved.class);
  }

  @Test
  public void credentialsAreReplaced() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenActiveUser(USER_ID);

    final Credential newCredential = Credential.from(USER_CREDENTIAL.authority, "updated-user-credentials-id", "updated-user-credentials-secret", "updated-user-credentials-type");
    final UserState state = userOf(USER_ID).replaceCredential(newCredential).await();

    assertEquals(USER_ID, state.userId);
    assertEquals(new HashSet<>(Arrays.asList(newCredential)), state.credentials);
    assertEventDispatched(dispatcherAccess, 2, UserCredentialReplaced.class);
  }

  @Test
  public void profileIsReplaced() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    givenActiveUser(USER_ID);

    final Profile updatedProfile = Profile.from("updated-user-profile-emailAddress", PersonName.from("updated-user-profile-name-given", "updated-user-profile-name-family", "updated-user-profile-name-second"), "updated-user-profile-phone");
    final UserState state = userOf(USER_ID).replaceProfile(updatedProfile).await();

    assertEquals(updatedProfile, state.profile);
    assertEventDispatched(dispatcherAccess, 2, UserProfileReplaced.class);
  }

  private User userOf(UserId userId) {
    return world.actorFor(User.class, UserEntity.class, userId);
  }

  private void givenInactiveUser(UserId userId) {
    userOf(userId).registerUser(USER_USERNAME, USER_PROFILE, USER_CREDENTIALS, false).await();
  }

  private void givenActiveUser(UserId userId) {
    userOf(userId).registerUser(USER_USERNAME, USER_PROFILE, USER_CREDENTIALS, true).await();
  }

  private void assertEventDispatched(final AccessSafely dispatcherAccess, final int sequence, final Class<?> expectedEvent) {
    assertEquals(sequence, (int) dispatcherAccess.readFrom("entriesCount"), String.format("Expected at least %d events", sequence));
    assertEquals(expectedEvent.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", sequence - 1)).typeName(), String.format("Expected the %d event in the sequence to be %s", sequence, expectedEvent.getName()));
  }
}
