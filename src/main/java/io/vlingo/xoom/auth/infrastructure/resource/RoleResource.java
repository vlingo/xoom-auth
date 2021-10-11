package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.ContentType;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.auth.infrastructure.persistence.QueryModelStateStoreProvider;
import io.vlingo.xoom.auth.model.role.RoleEntity;
import io.vlingo.xoom.auth.infrastructure.persistence.RoleQueries;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.model.role.Role;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;

/**
 * See <a href="https://docs.vlingo.io/xoom-turbo/xoom-annotations#resourcehandlers">@ResourceHandlers</a>
 */
public class RoleResource extends DynamicResourceHandler {
  private final Grid grid;
  private final RoleQueries $queries;

  public RoleResource(final Grid grid) {
      super(grid.world().stage());
      this.grid = grid;
      this.$queries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).roleQueries;
  }

  public Completes<Response> provisionRole(final RoleData data) {
    return Role.provisionRole(grid, data.tenantId, data.name, data.description)
      .andThenTo(state -> Completes.withSuccess(entityResponseOf(Created, ResponseHeader.headers(ResponseHeader.of(Location, location(state.id))), serialized(RoleData.from(state))))
      .otherwise(arg -> Response.of(NotFound))
      .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())));
  }

  public Completes<Response> changeDescription(final String tenantId, final String roleName, final RoleData data) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.changeDescription(data.tenantId, data.name, data.description))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> assignGroup(final String tenantId, final String roleName, final RoleData data) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.assignGroup(data.tenantId, data.name))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> unassignGroup(final String tenantId, final String roleName, final String groupName, final RoleData data) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.unassignGroup(data.tenantId, data.name))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> assignUser(final String tenantId, final String roleName, final RoleData data) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.assignUser(data.tenantId, data.name))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> unassignUser(final String tenantId, final String roleName, final String username, final RoleData data) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.unassignUser(data.tenantId, data.name))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> attach(final String tenantId, final String roleName, final RoleData data) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.attach(data.tenantId, data.name))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> detach(final String tenantId, final String roleName, final String permissionName, final RoleData data) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.detach(data.tenantId, data.name))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> roles() {
    return $queries.roles()
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> roleOf(final String id) {
    return $queries.roleOf(id)
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  @Override
  public Resource<?> routes() {
     return resource("RoleResource",
        io.vlingo.xoom.http.resource.ResourceBuilder.post("/tenants/{tenantId}/roles")
            .body(RoleData.class)
            .handle(this::provisionRole),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/roles/{roleName}/description")
            .param(String.class)
            .param(String.class)
            .body(RoleData.class)
            .handle(this::changeDescription),
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/roles/{roleName}/groups")
            .param(String.class)
            .param(String.class)
            .body(RoleData.class)
            .handle(this::assignGroup),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/roles/{roleName}/groups/{groupName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .body(RoleData.class)
            .handle(this::unassignGroup),
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/roles/{roleName}/users")
            .param(String.class)
            .param(String.class)
            .body(RoleData.class)
            .handle(this::assignUser),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/roles/{roleName}/users/{username}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .body(RoleData.class)
            .handle(this::unassignUser),
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/roles/{roleName}/permissions")
            .param(String.class)
            .param(String.class)
            .body(RoleData.class)
            .handle(this::attach),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/roles/{roleName}/permissions/{permissionName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .body(RoleData.class)
            .handle(this::detach),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/roles")
            .handle(this::roles),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/roles/{id}")
            .param(String.class)
            .handle(this::roleOf)
     );
  }

  @Override
  protected ContentType contentType() {
    return ContentType.of("application/json", "charset=UTF-8");
  }

  private String location(final String id) {
    return "/tenants/" + id;
  }

  private Completes<Role> resolve(final String tenantId, String roleName) {
    final Address address = grid.addressFactory().from(tenantId);
    return grid.actorOf(Role.class, address, Definition.has(RoleEntity.class, Definition.parameters(tenantId)));
  }

}
