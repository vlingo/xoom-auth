package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.auth.model.value.Credential;
import io.vlingo.xoom.auth.model.value.Profile;
import io.vlingo.xoom.common.Completes;

import java.util.Set;

public interface User {

  Completes<UserState> registerUser(final String username, final Profile profile, final Set<Credential> credentials, final boolean active);

  Completes<UserState> activate();

  Completes<UserState> deactivate();

  Completes<UserState> addCredential(final Credential credential);

  Completes<UserState> removeCredential(final Credential credential);

  Completes<UserState> replaceCredential(final Credential credential);

  Completes<UserState> replaceProfile(final Profile profile);

}