package io.vlingo.xoom.auth.model.user;

import java.util.*;
import io.vlingo.xoom.auth.model.value.*;

public final class UserState {

  public final String id;
  public final String tenantId;
  public final String username;
  public final boolean active;
  public final Set<Credential> credentials = new HashSet<>();
  public final Profile profile;

  public static UserState identifiedBy(final String id) {
    return new UserState(id, null, null, false, new HashSet<>(), null);
  }

  public UserState (final String id, final String tenantId, final String username, final boolean active, final Set<Credential> credentials, final Profile profile) {
    this.id = id;
    this.tenantId = tenantId;
    this.username = username;
    this.active = active;
    this.credentials.addAll(credentials);
    this.profile = profile;
  }

  public UserState registerUser(final String tenantId, final String username, final boolean active, final Set<Credential> credentials, final Profile profile) {
    return new UserState(this.id, tenantId, username, active, credentials, profile);
  }

  public UserState activate(final String tenantId, final String username) {
    //TODO: Implement command logic.
    return new UserState(this.id, tenantId, username, this.active, this.credentials, this.profile);
  }

  public UserState deactivate(final String tenantId, final String username) {
    //TODO: Implement command logic.
    return new UserState(this.id, tenantId, username, this.active, this.credentials, this.profile);
  }

  public UserState addCredential(final Credential credential) {
    //TODO: Implement command logic.
    this.credentials.add(credential);
    return new UserState(this.id, this.tenantId, this.username, this.active, this.credentials, this.profile);
  }

  public UserState removeCredential(final Credential credential) {
    //TODO: Implement command logic.
    this.credentials.remove(credential);
    return new UserState(this.id, this.tenantId, this.username, this.active, this.credentials, this.profile);
  }

  public UserState replaceCredential(final Credential credential) {
    //TODO: Implement command logic.
    this.credentials.add(credential);
    return new UserState(this.id, this.tenantId, this.username, this.active, this.credentials, this.profile);
  }

  public UserState replaceProfile(final String tenantId, final String username, final Profile profile) {
    //TODO: Implement command logic.
    return new UserState(this.id, tenantId, username, this.active, this.credentials, profile);
  }

}
