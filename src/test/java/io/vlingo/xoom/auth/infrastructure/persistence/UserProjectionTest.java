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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.model.user.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserProjectionTest {

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
    StatefulTypeRegistry statefulTypeRegistry = StatefulTypeRegistry.registerAll(world, stateStore, UserRegistrationData.class);
    QueryModelStateStoreProvider.using(world.stage(), statefulTypeRegistry);
    projection = world.actorFor(Projection.class, UserProjectionActor.class, stateStore);
  }

  private void registerExampleUser(UserState firstData, UserState secondData) {
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(2);
    projection.projectWith(createUserRegistered(firstData), control);
    projection.projectWith(createUserRegistered(secondData), control);
  }

  @Test
  public void registerUser() {
    final UserRegistrationData firstData = UserRegistrationData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-emailAddress", "first-user-profile-phone"));
    final UserRegistrationData secondData = UserRegistrationData.from("2", "second-user-tenantId", "second-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-emailAddress", "second-user-profile-phone"));

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(2);
    projection.projectWith(createUserRegistered(firstData.toUserState()), control);
    projection.projectWith(createUserRegistered(secondData.toUserState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(2, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));
    assertEquals(1, valueOfProjectionIdFor(secondData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, UserRegistrationData.class, interest);
    UserRegistrationData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(item.id, "1");
    assertEquals(item.tenantId, "first-user-tenantId");
    assertEquals(item.username, "first-user-username");
    assertEquals(item.active, true);
    assertEquals(item.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(item.profile.name.given, "first-user-profile-name-given");
    assertEquals(item.profile.name.family, "first-user-profile-name-family");
    assertEquals(item.profile.name.second, "first-user-profile-name-second");
    assertEquals(item.profile.phone, "first-user-profile-phone");

    interest = new CountingReadResultInterest();
    interestAccess = interest.afterCompleting(1);
    stateStore.read(secondData.id, UserRegistrationData.class, interest);
    item = interestAccess.readFrom("item", secondData.id);
    assertEquals(secondData.id, item.id);
    assertEquals(item.id, "2");
    assertEquals(item.tenantId, "second-user-tenantId");
    assertEquals(item.username, "second-user-username");
    assertEquals(item.active, true);
    assertEquals(item.profile.emailAddress, "second-user-profile-emailAddress");
    assertEquals(item.profile.name.given, "second-user-profile-name-given");
    assertEquals(item.profile.name.family, "second-user-profile-name-family");
    assertEquals(item.profile.name.second, "second-user-profile-name-second");
    assertEquals(item.profile.phone, "second-user-profile-phone");
  }

  @Test
  public void activate() {
    final UserRegistrationData firstData = UserRegistrationData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-emailAddress", "first-user-profile-phone"));
    final UserRegistrationData secondData = UserRegistrationData.from("2", "second-user-tenantId", "second-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-emailAddress", "second-user-profile-phone"));
    registerExampleUser(firstData.toUserState(), secondData.toUserState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createUserActivated(firstData.toUserState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, UserRegistrationData.class, interest);
    UserRegistrationData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(item.id, "1");
    assertEquals(item.tenantId, "first-user-tenantId");
    assertEquals(item.username, "first-user-username");
  }

  @Test
  public void deactivate() {
    final UserRegistrationData firstData = UserRegistrationData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-emailAddress", "first-user-profile-phone"));
    final UserRegistrationData secondData = UserRegistrationData.from("2", "second-user-tenantId", "second-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-emailAddress", "second-user-profile-phone"));
    registerExampleUser(firstData.toUserState(), secondData.toUserState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createUserDeactivated(firstData.toUserState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, UserRegistrationData.class, interest);
    UserRegistrationData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(item.id, "1");
    assertEquals(item.tenantId, "first-user-tenantId");
    assertEquals(item.username, "first-user-username");
  }

  @Test
  public void addCredential() {
    final UserRegistrationData firstData = UserRegistrationData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-emailAddress", "first-user-profile-phone"));
    final UserRegistrationData secondData = UserRegistrationData.from("2", "second-user-tenantId", "second-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-emailAddress", "second-user-profile-phone"));
    registerExampleUser(firstData.toUserState(), secondData.toUserState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createUserCredentialAdded(firstData.toUserState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, UserRegistrationData.class, interest);
    UserRegistrationData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(item.id, "1");
    assertNotNull(item.credentials);
  }

  @Test
  public void removeCredential() {
    final UserRegistrationData firstData = UserRegistrationData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-emailAddress", "first-user-profile-phone"));
    final UserRegistrationData secondData = UserRegistrationData.from("2", "second-user-tenantId", "second-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-emailAddress", "second-user-profile-phone"));
    registerExampleUser(firstData.toUserState(), secondData.toUserState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createUserCredentialRemoved(firstData.toUserState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, UserRegistrationData.class, interest);
    UserRegistrationData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(item.id, "1");
    assertNotNull(item.credentials);
  }

  @Test
  public void replaceCredential() {
    final UserRegistrationData firstData = UserRegistrationData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-emailAddress", "first-user-profile-phone"));
    final UserRegistrationData secondData = UserRegistrationData.from("2", "second-user-tenantId", "second-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-emailAddress", "second-user-profile-phone"));
    registerExampleUser(firstData.toUserState(), secondData.toUserState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createUserCredentialReplaced(firstData.toUserState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, UserRegistrationData.class, interest);
    UserRegistrationData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(item.id, "1");
    assertNotNull(item.credentials);
  }

  @Test
  public void replaceProfile() {
    final UserRegistrationData firstData = UserRegistrationData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-emailAddress", "first-user-profile-phone"));
    final UserRegistrationData secondData = UserRegistrationData.from("2", "second-user-tenantId", "second-user-username", true, new HashSet<>(), ProfileData.from(PersonNameData.from("second-user-profile-name-given", "second-user-profile-name-family", "second-user-profile-name-second"), "second-user-profile-emailAddress", "second-user-profile-phone"));
    registerExampleUser(firstData.toUserState(), secondData.toUserState());

    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createUserProfileReplaced(firstData.toUserState()), control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstData.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstData.id, UserRegistrationData.class, interest);
    UserRegistrationData item = interestAccess.readFrom("item", firstData.id);

    assertEquals(item.id, "1");
    assertEquals(item.tenantId, "first-user-tenantId");
    assertEquals(item.username, "first-user-username");
    assertEquals(item.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(item.profile.name.given, "first-user-profile-name-given");
    assertEquals(item.profile.name.family, "first-user-profile-name-family");
    assertEquals(item.profile.name.second, "first-user-profile-name-second");
    assertEquals(item.profile.phone, "first-user-profile-phone");
  }

  private int valueOfProjectionIdFor(final String valueText, final Map<String, Integer> confirmations) {
    return confirmations.get(valueToProjectionId.get(valueText));
  }

  private Projectable createUserRegistered(UserState data) {
    final UserRegistered eventData = new UserRegistered(data.id, data.tenantId, data.username, data.active, data.credentials, data.profile);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserRegistered.class, 1, JsonSerialization.serialized(eventData), 1, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createUserActivated(UserState data) {
    final UserActivated eventData = new UserActivated(data.id, data.tenantId, data.username);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserActivated.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createUserDeactivated(UserState data) {
    final UserDeactivated eventData = new UserDeactivated(data.id, data.tenantId, data.username);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserDeactivated.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createUserCredentialAdded(UserState data) {
    final UserCredentialAdded eventData = new UserCredentialAdded(data.id, data.credentials.stream().findFirst().orElse(null));

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserCredentialAdded.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createUserCredentialRemoved(UserState data) {
    final UserCredentialRemoved eventData = new UserCredentialRemoved(data.id, data.credentials.stream().findFirst().orElse(null));

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserCredentialRemoved.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createUserCredentialReplaced(UserState data) {
    final UserCredentialReplaced eventData = new UserCredentialReplaced(data.id, data.credentials.stream().findFirst().orElse(null));

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserCredentialReplaced.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createUserProfileReplaced(UserState data) {
    final UserProfileReplaced eventData = new UserProfileReplaced(data.id, data.tenantId, data.username, data.profile);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(UserProfileReplaced.class, 1, JsonSerialization.serialized(eventData), 2, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

}
