package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.auth.model.value.Credential;
import io.vlingo.xoom.auth.model.value.Profile;

import java.util.HashSet;
import java.util.Set;

public final class UserState {

  public final UserId userId;
  public final String username;
  public final boolean active;
  public final Set<Credential> credentials = new HashSet<>();
  public final Profile profile;

  public static UserState identifiedBy(final UserId userId) {
    return new UserState(userId, null, null, new HashSet<>(), false);
  }

  public UserState(final UserId userId, final String username, final Profile profile, final Set<Credential> credentials, final boolean active) {
    this.userId = userId;
    this.username = username;
    this.active = active;
    this.credentials.addAll(credentials);
    this.profile = profile;
  }

  public UserState registerUser(final String username, final Profile profile, final Set<Credential> credentials, final boolean active) {
    return new UserState(this.userId, username, profile, credentials, active);
  }

  public UserState activate() {
    return new UserState(this.userId, this.username, this.profile, this.credentials, true);
  }

  public UserState deactivate() {
    return new UserState(this.userId, this.username, this.profile, this.credentials, false);
  }

  public UserState addCredential(final Credential credential) {
    this.credentials.add(credential);
    return new UserState(this.userId, this.username, this.profile, this.credentials, this.active);
  }

  public UserState removeCredential(final Credential credential) {
    this.credentials.remove(credential);
    return new UserState(this.userId, this.username, this.profile, this.credentials, this.active);
  }

  public UserState replaceCredential(final Credential credential) {
    this.credentials.stream().filter(c -> c.id.equals(credential.id))
            .findFirst()
            .ifPresent(c -> this.credentials.remove(c));
    this.credentials.add(credential);
    return new UserState(this.userId, this.username, this.profile, this.credentials, this.active);
  }

  public UserState replaceProfile(final Profile profile) {
    return new UserState(this.userId, this.username, profile, this.credentials, this.active);
  }

}
