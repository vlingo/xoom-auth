package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.actors.Definition;
import java.util.*;
import io.vlingo.xoom.auth.model.value.*;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.actors.Stage;

public interface User {

  Completes<UserState> registerUser(final String tenantId, final String username, final boolean active, final Set<Credential> credentials, final Profile profile);

  static Completes<UserState> registerUser(final Stage stage, final String tenantId, final String username, final boolean active, final Set<Credential> credentials, final Profile profile) {
    final io.vlingo.xoom.actors.Address _address = stage.addressFactory().uniquePrefixedWith("g-");
    final User _user = stage.actorFor(User.class, Definition.has(UserEntity.class, Definition.parameters(_address.idString())), _address);
    return _user.registerUser(tenantId, username, active, credentials, profile);
  }

  Completes<UserState> activate(final String tenantId, final String username);

  Completes<UserState> deactivate(final String tenantId, final String username);

  Completes<UserState> addCredential(final Credential credential);

  Completes<UserState> removeCredential(final Credential credential);

  Completes<UserState> replaceCredential(final Credential credential);

  Completes<UserState> replaceProfile(final String tenantId, final String username, final Profile profile);

}