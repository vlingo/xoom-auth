package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.value.Credential;
import io.vlingo.xoom.auth.model.value.PersonName;
import io.vlingo.xoom.auth.model.value.Profile;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static io.vlingo.xoom.auth.test.Assertions.assertEventDispatched;
import static org.junit.jupiter.api.Assertions.*;

public class UserEntityTest {

  private final TenantId TENANT_ID = TenantId.from("8b727090-652a-4b02-90fc-2e890d09a1c5");
  private final String USER_USERNAME = "bob";
  private final Profile USER_PROFILE = Profile.from("bob@example.com", PersonName.from("Bob", "Smith", "Cecil"), "07926123123");
  private final Credential USER_CREDENTIAL = Credential.xoomCredentialFrom("user-credential-authority", "user-credential-id", "user-credential-secret");
  private final Set<Credential> USER_CREDENTIALS = Collections.singleton(USER_CREDENTIAL);
  private final UserId USER_ID = UserId.from(TENANT_ID, USER_USERNAME);

  private World world;
  private MockDispatcher dispatcher;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp(){
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    final EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);
    entryAdapterProvider.registerAdapter(UserRegistered.class, new UserRegisteredAdapter());
    entryAdapterProvider.registerAdapter(UserActivated.class, new UserActivatedAdapter());
    entryAdapterProvider.registerAdapter(UserDeactivated.class, new UserDeactivatedAdapter());
    entryAdapterProvider.registerAdapter(UserCredentialAdded.class, new UserCredentialAddedAdapter());
    entryAdapterProvider.registerAdapter(UserCredentialRemoved.class, new UserCredentialRemovedAdapter());
    entryAdapterProvider.registerAdapter(UserCredentialReplaced.class, new UserCredentialReplacedAdapter());
    entryAdapterProvider.registerAdapter(UserProfileReplaced.class, new UserProfileReplacedAdapter());

    final Journal<String> journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, UserEntity.class, UserEntity.class.getSimpleName()));
  }

  @AfterEach
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void userIsRegistered() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Completes<UserState> outcome = userOf(USER_ID)
            .registerUser(USER_USERNAME, USER_PROFILE, USER_CREDENTIALS, true);

    assertCompletes(outcome, state -> {
      assertEquals(USER_ID, state.userId);
      assertEquals(USER_USERNAME, state.username);
      assertEquals(true, state.active);
      assertEquals(USER_PROFILE, state.profile);
      assertEquals(USER_CREDENTIALS, state.credentials);
      assertEventDispatched(dispatcherAccess, 1, UserRegistered.class);
    });
  }

  @Test
  public void userIsActivated() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<UserState> outcome = givenInactiveUser(USER_ID)
            .andThenTo(u -> userOf(USER_ID).activate());

    assertCompletes(outcome, state -> {
      assertTrue(state.active);
      assertEventDispatched(dispatcherAccess, 2, UserActivated.class);
    });
  }

  @Test
  public void userIsDeactivated() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<UserState> outcome = givenActiveUser(USER_ID)
            .andThenTo(u -> userOf(USER_ID).deactivate());

    assertCompletes(outcome, state -> {
      assertFalse(state.active);
      assertEventDispatched(dispatcherAccess, 2, UserDeactivated.class);
    });
  }

  @Test
  public void credentialsAreAddedToRegisteredUser() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Credential newCredential = Credential.xoomCredentialFrom("updated-user-credentials-authority", "updated-1", "updated-user-credentials-secret");

    final Completes<UserState> outcome = givenActiveUser(USER_ID)
            .andThenTo(u -> userOf(USER_ID).addCredential(newCredential));

    assertCompletes(outcome, state -> {
      assertEquals(USER_ID, state.userId);
      assertContainsCredential(USER_CREDENTIAL, state);
      assertContainsCredential(newCredential, state);
      assertEventDispatched(dispatcherAccess, 2, UserCredentialAdded.class);
    });
  }

  @Test
  public void credentialsAreRemovedFromRegisteredUser() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);

    final Completes<UserState> outcome = givenActiveUser(USER_ID)
            .andThenTo(u -> userOf(USER_ID).removeCredential(USER_CREDENTIAL.authority));

    assertCompletes(outcome, state -> {
      assertEquals(USER_ID, state.userId);
      assertNotContainsCredential(USER_CREDENTIAL, state);
      assertEquals(0, state.credentials.size());
      assertEventDispatched(dispatcherAccess, 2, UserCredentialRemoved.class);
    });
  }

  @Test
  public void credentialsAreReplaced() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Credential newCredential = Credential.xoomCredentialFrom(USER_CREDENTIAL.authority, "updated-user-credentials-id", "updated-user-credentials-secret");

    final Completes<UserState> outcome = givenActiveUser(USER_ID)
            .andThenTo(u -> userOf(USER_ID).replaceCredential(newCredential));

    assertCompletes(outcome, state -> {
      assertEquals(USER_ID, state.userId);
      assertContainsCredential(newCredential, state);
      assertNotContainsCredential(USER_CREDENTIAL, state);
      assertEventDispatched(dispatcherAccess, 2, UserCredentialReplaced.class);
    });
  }

  @Test
  public void profileIsReplaced() {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Profile updatedProfile = Profile.from("updated-user-profile-emailAddress", PersonName.from("updated-user-profile-name-given", "updated-user-profile-name-family", "updated-user-profile-name-second"), "updated-user-profile-phone");

    final Completes<UserState> outcome = givenActiveUser(USER_ID)
            .andThenTo(u -> userOf(USER_ID).replaceProfile(updatedProfile));

    assertCompletes(outcome, state -> {
      assertEquals(updatedProfile, state.profile);
      assertEventDispatched(dispatcherAccess, 2, UserProfileReplaced.class);
    });
  }

  private User userOf(final UserId userId) {
    return world.actorFor(User.class, UserEntity.class, userId);
  }

  private Completes<UserState> givenInactiveUser(final UserId userId) {
    return userOf(userId).registerUser(USER_USERNAME, USER_PROFILE, USER_CREDENTIALS, false);
  }

  private Completes<UserState> givenActiveUser(final UserId userId) {
    return userOf(userId).registerUser(USER_USERNAME, USER_PROFILE, USER_CREDENTIALS, true);
  }

  private void assertContainsCredential(final Credential credential, final UserState state) {
    Optional<Credential> foundCredential = state.credentials.stream().filter(c -> c.equals(credential)).findFirst();
    assertTrue(foundCredential.isPresent(), String.format("Credential not found %s", credential));
  }

  private void assertNotContainsCredential(final Credential credential, final UserState state) {
    Optional<Credential> foundCredential = state.credentials.stream().filter(c -> c.equals(credential)).findFirst();
    assertFalse(foundCredential.isPresent(), String.format("Credential found %s", credential));
  }
}
