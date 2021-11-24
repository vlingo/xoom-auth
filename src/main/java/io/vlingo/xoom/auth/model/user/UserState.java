package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.auth.model.value.Credential;
import io.vlingo.xoom.auth.model.value.Profile;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class UserState {

  public final UserId userId;
  public final String username;
  public final boolean active;
  public final Set<Credential> credentials;
  public final Profile profile;

  public static UserState identifiedBy(final UserId userId) {
    return new UserState(userId, null, null, Collections.emptySet(), false);
  }

  public UserState(final UserId userId, final String username, final Profile profile, final Set<Credential> credentials, final boolean active) {
    this.userId = userId;
    this.username = username;
    this.active = active;
    this.credentials = Collections.unmodifiableSet(credentials);
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
    return new UserState(this.userId, this.username, this.profile, includeCredential(this.credentials, credential), this.active);
  }

  public UserState removeCredential(final String authority) {
    return new UserState(this.userId, this.username, this.profile, removeCredential(this.credentials, authority), this.active);
  }

  public UserState replaceCredential(final String authority, final Credential credential) {
    return new UserState(this.userId, this.username, this.profile, includeCredential(removeCredential(this.credentials, authority), credential), this.active);
  }

  public UserState replaceProfile(final Profile profile) {
    return new UserState(this.userId, this.username, profile, this.credentials, this.active);
  }

  private Set<Credential> includeCredential(final Set<Credential> credentials, final Credential credential) {
    return Stream.concat(credentials.stream(), Stream.of(credential)).collect(Collectors.toSet());
  }

  private Set<Credential> removeCredential(final Set<Credential> credentials, final String authority) {
    return credentials.stream().filter(c -> !c.authority.equals(authority)).collect(Collectors.toSet());
  }
}
