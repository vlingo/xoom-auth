package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.turbo.annotation.autodispatch.*;
import io.vlingo.xoom.http.Response;

import io.vlingo.xoom.auth.infrastructure.persistence.UserQueriesActor;
import io.vlingo.xoom.auth.infrastructure.persistence.UserQueries;
import io.vlingo.xoom.auth.model.user.User;
import io.vlingo.xoom.auth.model.user.UserEntity;
import io.vlingo.xoom.auth.infrastructure.UserRegistrationData;

import static io.vlingo.xoom.http.Method.*;

@AutoDispatch(path="/tenants", handlers=UserResourceHandlers.class)
@Queries(protocol = UserQueries.class, actor = UserQueriesActor.class)
@Model(protocol = User.class, actor = UserEntity.class, data = UserRegistrationData.class)
public interface UserResource {

  @Route(method = POST, path = "/{tenantId}/users", handler = UserResourceHandlers.REGISTER_USER)
  @ResponseAdapter(handler = UserResourceHandlers.ADAPT_STATE)
  Completes<Response> registerUser(@Body final UserRegistrationData data);

  @Route(method = PATCH, path = "/{tenantId}/users/{username}/activate", handler = UserResourceHandlers.ACTIVATE)
  @ResponseAdapter(handler = UserResourceHandlers.ADAPT_STATE)
  Completes<Response> activate(@Id final String id, @Body final UserRegistrationData data);

  @Route(method = PATCH, path = "/{tenantId}/users/{username}/deactivate", handler = UserResourceHandlers.DEACTIVATE)
  @ResponseAdapter(handler = UserResourceHandlers.ADAPT_STATE)
  Completes<Response> deactivate(@Id final String id, @Body final UserRegistrationData data);

  @Route(method = PUT, path = "/{tenantId}/users/{username}/credentials", handler = UserResourceHandlers.ADD_CREDENTIAL)
  @ResponseAdapter(handler = UserResourceHandlers.ADAPT_STATE)
  Completes<Response> addCredential(@Id final String id, @Body final UserRegistrationData data);

  @Route(method = DELETE, path = "/{tenantId}/users/{username}/credentials/{authority}", handler = UserResourceHandlers.REMOVE_CREDENTIAL)
  @ResponseAdapter(handler = UserResourceHandlers.ADAPT_STATE)
  Completes<Response> removeCredential(@Id final String id, @Body final UserRegistrationData data);

  @Route(method = PATCH, path = "/{tenantId}/users/{username}/credentials/{authority}", handler = UserResourceHandlers.REPLACE_CREDENTIAL)
  @ResponseAdapter(handler = UserResourceHandlers.ADAPT_STATE)
  Completes<Response> replaceCredential(@Id final String id, @Body final UserRegistrationData data);

  @Route(method = PATCH, path = "/{tenantId}/users/{username}/profile", handler = UserResourceHandlers.REPLACE_PROFILE)
  @ResponseAdapter(handler = UserResourceHandlers.ADAPT_STATE)
  Completes<Response> replaceProfile(@Id final String id, @Body final UserRegistrationData data);

  @Route(method = GET, handler = UserResourceHandlers.USERS)
  Completes<Response> users();

  @Route(method = GET, path = "/{id}", handler = UserResourceHandlers.USER_OF)
  Completes<Response> userOf(final String id);

}