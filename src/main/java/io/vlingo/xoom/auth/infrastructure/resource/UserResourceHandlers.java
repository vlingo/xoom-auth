package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.turbo.annotation.autodispatch.Handler.Three;
import io.vlingo.xoom.turbo.annotation.autodispatch.Handler.Two;
import io.vlingo.xoom.turbo.annotation.autodispatch.HandlerEntry;

import io.vlingo.xoom.auth.infrastructure.persistence.UserQueries;
import io.vlingo.xoom.auth.model.user.User;
import io.vlingo.xoom.auth.model.value.*;
import io.vlingo.xoom.auth.model.user.UserState;
import io.vlingo.xoom.auth.infrastructure.*;
import java.util.Collection;

public class UserResourceHandlers {

  public static final int REGISTER_USER = 0;
  public static final int ACTIVATE = 1;
  public static final int DEACTIVATE = 2;
  public static final int ADD_CREDENTIAL = 3;
  public static final int REMOVE_CREDENTIAL = 4;
  public static final int REPLACE_CREDENTIAL = 5;
  public static final int REPLACE_PROFILE = 6;
  public static final int USERS = 7;
  public static final int USER_OF = 8;
  public static final int ADAPT_STATE = 9;

  public static final HandlerEntry<Three<Completes<UserState>, Stage, UserRegistrationData>> REGISTER_USER_HANDLER =
          HandlerEntry.of(REGISTER_USER, ($stage, data) -> {
              final PersonName name = PersonName.from(data.profile.name.given, data.profile.name.family, data.profile.name.second);
              final Profile profile = Profile.from(data.profile.emailAddress, name, data.profile.phone);
              return User.registerUser($stage, data.tenantId, data.username, data.active, profile);
          });

  public static final HandlerEntry<Three<Completes<UserState>, User, UserRegistrationData>> ACTIVATE_HANDLER =
          HandlerEntry.of(ACTIVATE, (user, data) -> {
              return user.activate(data.tenantId, data.username);
          });

  public static final HandlerEntry<Three<Completes<UserState>, User, UserRegistrationData>> DEACTIVATE_HANDLER =
          HandlerEntry.of(DEACTIVATE, (user, data) -> {
              return user.deactivate(data.tenantId, data.username);
          });

  public static final HandlerEntry<Three<Completes<UserState>, User, UserRegistrationData>> ADD_CREDENTIAL_HANDLER =
          HandlerEntry.of(ADD_CREDENTIAL, (user, data) -> {
              return user.addCredential(data.credentials.stream().map(CredentialData::toCredential).findFirst().get());
          });

  public static final HandlerEntry<Three<Completes<UserState>, User, UserRegistrationData>> REMOVE_CREDENTIAL_HANDLER =
          HandlerEntry.of(REMOVE_CREDENTIAL, (user, data) -> {
              return user.removeCredential(data.credentials.stream().map(CredentialData::toCredential).findFirst().get());
          });

  public static final HandlerEntry<Three<Completes<UserState>, User, UserRegistrationData>> REPLACE_CREDENTIAL_HANDLER =
          HandlerEntry.of(REPLACE_CREDENTIAL, (user, data) -> {
              return user.replaceCredential(data.credentials.stream().map(CredentialData::toCredential).findFirst().get());
          });

  public static final HandlerEntry<Three<Completes<UserState>, User, UserRegistrationData>> REPLACE_PROFILE_HANDLER =
          HandlerEntry.of(REPLACE_PROFILE, (user, data) -> {
              final PersonName name = PersonName.from(data.profile.name.given, data.profile.name.family, data.profile.name.second);
              final Profile profile = Profile.from(data.profile.emailAddress, name, data.profile.phone);
              return user.replaceProfile(data.tenantId, data.username, profile);
          });

  public static final HandlerEntry<Two<UserRegistrationData, UserState>> ADAPT_STATE_HANDLER =
          HandlerEntry.of(ADAPT_STATE, UserRegistrationData::from);

  public static final HandlerEntry<Two<Completes<Collection<UserRegistrationData>>, UserQueries>> QUERY_ALL_HANDLER =
          HandlerEntry.of(USERS, UserQueries::users);

  public static final HandlerEntry<Three<Completes<UserRegistrationData>, UserQueries, String>> QUERY_BY_ID_HANDLER =
          HandlerEntry.of(USER_OF, ($queries, id) -> $queries.userOf(id));

}