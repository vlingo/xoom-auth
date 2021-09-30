package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.auth.model.value.Credential;
import io.vlingo.xoom.auth.model.value.PersonName;
import io.vlingo.xoom.auth.model.value.Profile;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.ContentType;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.auth.infrastructure.persistence.QueryModelStateStoreProvider;
import io.vlingo.xoom.auth.infrastructure.persistence.UserQueries;
import io.vlingo.xoom.auth.model.user.User;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.model.user.UserEntity;

import java.util.Set;
import java.util.stream.Collectors;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;

/**
 * See <a href="https://docs.vlingo.io/xoom-turbo/xoom-annotations#resourcehandlers">@ResourceHandlers</a>
 */
public class UserResource extends DynamicResourceHandler {
  private final Grid grid;
  private final UserQueries $queries;

  public UserResource(final Grid grid) {
      super(grid.world().stage());
      this.grid = grid;
      this.$queries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).userQueries;
  }

  public Completes<Response> registerUser(final UserRegistrationData data) {
    final PersonName name = PersonName.from(data.profile.name.given, data.profile.name.family, data.profile.name.second);
    final Profile profile = Profile.from(data.profile.emailAddress, name, data.profile.phone);
    final Set<Credential> credentials = data.credentials.stream().map(CredentialData::toCredential).collect(Collectors.toSet());
    return User.registerUser(grid, data.tenantId, data.username, data.active, credentials, profile)
      .andThenTo(state -> Completes.withSuccess(entityResponseOf(Created, ResponseHeader.headers(ResponseHeader.of(Location, location(state.id))), serialized(UserRegistrationData.from(state))))
      .otherwise(arg -> Response.of(NotFound))
      .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())));
  }

  public Completes<Response> activate(final String id, final UserRegistrationData data) {
    return resolve(id)
            .andThenTo(user -> user.activate(data.tenantId, data.username))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(UserRegistrationData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> deactivate(final String id, final UserRegistrationData data) {
    return resolve(id)
            .andThenTo(user -> user.deactivate(data.tenantId, data.username))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(UserRegistrationData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> addCredential(final String id, final UserRegistrationData data) {
    return resolve(id)
            .andThenTo(user -> user.addCredential(data.credentials.stream().map(CredentialData::toCredential).findFirst().get()))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(UserRegistrationData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> removeCredential(final String id, final UserRegistrationData data) {
    return resolve(id)
            .andThenTo(user -> user.removeCredential(data.credentials.stream().map(CredentialData::toCredential).findFirst().get()))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(UserRegistrationData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> replaceCredential(final String id, final UserRegistrationData data) {
    return resolve(id)
            .andThenTo(user -> user.replaceCredential(data.credentials.stream().map(CredentialData::toCredential).findFirst().get()))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(UserRegistrationData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> replaceProfile(final String id, final UserRegistrationData data) {
    final PersonName name = PersonName.from(data.profile.name.given, data.profile.name.family, data.profile.name.second);
    final Profile profile = Profile.from(data.profile.emailAddress, name, data.profile.phone);
    return resolve(id)
            .andThenTo(user -> user.replaceProfile(data.tenantId, data.username, profile))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(UserRegistrationData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> users() {
    return $queries.users()
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> userOf(final String id) {
    return $queries.userOf(id)
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  @Override
  public Resource<?> routes() {
     return resource("UserResource",
        io.vlingo.xoom.http.resource.ResourceBuilder.post("/tenants/{tenantId}/users")
            .body(UserRegistrationData.class)
            .handle(this::registerUser),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/users/{username}/activate")
            .param(String.class)
            .body(UserRegistrationData.class)
            .handle(this::activate),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/users/{username}/deactivate")
            .param(String.class)
            .body(UserRegistrationData.class)
            .handle(this::deactivate),
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/users/{username}/credentials")
            .param(String.class)
            .body(UserRegistrationData.class)
            .handle(this::addCredential),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/users/{username}/credentials/{authority}")
            .param(String.class)
            .param(UserRegistrationData.class)
            .handle(this::removeCredential),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/users/{username}/credentials/{authority}")
            .param(String.class)
            .body(UserRegistrationData.class)
            .handle(this::replaceCredential),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/users/{username}/profile")
            .param(String.class)
            .body(UserRegistrationData.class)
            .handle(this::replaceProfile),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/users")
            .handle(this::users),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/users/{id}")
            .param(String.class)
            .handle(this::userOf)
     );
  }

  @Override
  protected ContentType contentType() {
    return ContentType.of("application/json", "charset=UTF-8");
  }

  private String location(final String id) {
    return "/tenants/" + id;
  }

  private Completes<User> resolve(final String id) {
    final Address address = grid.addressFactory().from(id);
    return grid.actorOf(User.class, address, Definition.has(UserEntity.class, Definition.parameters(id)));
  }

}