package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.persistence.UserView.CredentialView;
import io.vlingo.xoom.auth.infrastructure.persistence.UserView.ProfileView;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.*;
import io.vlingo.xoom.auth.model.value.Credential;
import io.vlingo.xoom.auth.model.value.PersonName;
import io.vlingo.xoom.auth.model.value.Profile;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.projection.Projection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static io.vlingo.xoom.auth.test.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserProjectionTest extends ProjectionTest {

  @Override
  protected Set<Class<?>> statefulTypes() {
    return Collections.singleton(UserView.class);
  }

  @Override
  protected Projection projection() {
    return world.actorFor(Projection.class, UserProjectionActor.class, stateStore);
  }

  @Test
  public void itProjectsRegisteredUser() {
    final UserId userId = UserId.from(TenantId.unique(), "bobby");
    final Profile profile = Profile.from("bobby@example.com", PersonName.from("Bobby", "Tables", "Little"), "07777123123");
    final Set<Credential> credentials = Collections.singleton(Credential.xoomCredentialFrom("authority", "credential-id", "secret"));

    givenEvents(
            new UserRegistered(userId, "bobby", profile, credentials, false)
    );

    assertCompletes(userOf(userId), user -> {
      assertEquals(userId.idString(), user.id);
      assertEquals(userId.tenantId.idString(), user.tenantId);
      assertEquals(ProfileView.from(profile), user.profile);
      assertEquals(CredentialView.fromAll(credentials), user.credentials);
      assertFalse(user.active);
    });
  }

  @Test
  public void itProjectsUserActivation() {
    final UserId userId = UserId.from(TenantId.unique(), "bobby");

    givenEvents(
            new UserRegistered(userId, "bobby", profile(), credentials(), false),
            new UserActivated(userId)
    );

    assertCompletes(userOf(userId), user -> assertTrue(user.active));
  }

  @Test
  public void itProjectsUserDeactivation() {
    final UserId userId = UserId.from(TenantId.unique(), "bobby");

    givenEvents(
            new UserRegistered(userId, "bobby", profile(), credentials(), true),
            new UserDeactivated(userId)
    );

    assertCompletes(userOf(userId), user -> assertFalse(user.active));
  }

  @Test
  public void itProjectsAddedCredentials() {
    final UserId userId = UserId.from(TenantId.unique(), "bobby");
    final Set<Credential> registeredCredentials = Collections.singleton(Credential.xoomCredentialFrom("authority", "credential-id", "secret"));
    final Credential addedCredential = Credential.xoomCredentialFrom("authority-added", "credential-id-added", "secret-added");

    givenEvents(
            new UserRegistered(userId, "bobby", profile(), registeredCredentials, false),
            new UserCredentialAdded(userId, addedCredential)
    );

    assertCompletes(userOf(userId), user -> {
      assertContainsAll(CredentialView.fromAll(registeredCredentials), user.credentials);
      assertContains(CredentialView.from(addedCredential), user.credentials);
    });
  }

  @Test
  public void itProjectsRemovedCredentials() {
    final UserId userId = UserId.from(TenantId.unique(), "bobby");
    final Credential firstCredential = Credential.xoomCredentialFrom("authority-1", "credential-id-1", "secret-1");
    final Credential secondCredential = Credential.xoomCredentialFrom("authority-2", "credential-id-2", "secret-2");
    final Set<Credential> credentials = new HashSet<>(Arrays.asList(firstCredential, secondCredential));

    givenEvents(
            new UserRegistered(userId, "bobby", profile(), credentials, false),
            new UserCredentialRemoved(userId, "authority-1")
    );

    assertCompletes(userOf(userId), user -> {
      assertNotContains(CredentialView.from(firstCredential), user.credentials);
      assertContains(CredentialView.from(secondCredential), user.credentials);
    });
  }

  @Test
  public void itProjectsReplacementCredentials() {
    final UserId userId = UserId.from(TenantId.unique(), "bobby");
    final Credential firstCredential = Credential.xoomCredentialFrom("authority-1", "credential-id-1", "secret-1");
    final Credential secondCredential = Credential.xoomCredentialFrom("authority-2", "credential-id-2", "secret-2");
    final Credential replacementCredential = Credential.xoomCredentialFrom("authority-3", "credential-id-3", "secret-3");
    final Set<Credential> credentials = new HashSet<>(Arrays.asList(firstCredential, secondCredential));

    givenEvents(
            new UserRegistered(userId, "bobby", profile(), credentials, false),
            new UserCredentialReplaced(userId, "authority-1", replacementCredential)
    );

    assertCompletes(userOf(userId), user -> {
      assertNotContains(CredentialView.from(firstCredential), user.credentials);
      assertContains(CredentialView.from(secondCredential), user.credentials);
      assertContains(CredentialView.from(replacementCredential), user.credentials);
    });
  }

  @Test
  public void itProjectsReplacementProfile() {
    final UserId userId = UserId.from(TenantId.unique(), "bobby");
    final Profile profile = Profile.from("bobby@example.com", PersonName.from("Bobby", "Tables", "Little"), "07777123123");
    final Profile replacementProfile = Profile.from("alice@example.com", PersonName.from("Alice", "Green", "Gabrielle"), "07777999888");

    givenEvents(
            new UserRegistered(userId, "bobby", profile, credentials(), false),
            new UserProfileReplaced(userId, replacementProfile)
    );

    assertCompletes(userOf(userId), user -> assertEquals(ProfileView.from(replacementProfile), user.profile));
  }

  private Completes<UserView> userOf(final UserId userId) {
    return world.actorFor(UserQueries.class, UserQueriesActor.class, stateStore).userOf(userId);
  }

  private Set<Credential> credentials() {
    return Collections.singleton(Credential.xoomCredentialFrom("authority", "credential-id", "secret"));
  }

  private Profile profile() {
    return Profile.from("bobby@example.com", PersonName.from("Bobby", "Tables", "Little"), "07777123123");
  }
}
