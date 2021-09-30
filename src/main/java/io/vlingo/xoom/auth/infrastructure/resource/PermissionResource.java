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
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionQueries;
import io.vlingo.xoom.auth.model.*;
import io.vlingo.xoom.auth.model.permission.Permission;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.model.permission.PermissionEntity;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;

/**
 * See <a href="https://docs.vlingo.io/xoom-turbo/xoom-annotations#resourcehandlers">@ResourceHandlers</a>
 */
public class PermissionResource extends DynamicResourceHandler {
  private final Grid grid;
  private final PermissionQueries $queries;

  public PermissionResource(final Grid grid) {
      super(grid.world().stage());
      this.grid = grid;
      this.$queries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).permissionQueries;
  }

  public Completes<Response> provisionPermission(final PermissionData data) {
    return Permission.provisionPermission(grid, data.description, data.name, data.tenantId)
      .andThenTo(state -> Completes.withSuccess(entityResponseOf(Created, ResponseHeader.headers(ResponseHeader.of(Location, location(state.id))), serialized(PermissionData.from(state))))
      .otherwise(arg -> Response.of(NotFound))
      .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())));
  }

  public Completes<Response> enforce(final String id, final PermissionData data) {
    return resolve(id)
            .andThenTo(permission -> permission.enforce(data.constraints.stream().map(ConstraintData::toConstraint).findFirst().get()))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(PermissionData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> enforceReplacement(final String id, final PermissionData data) {
    return resolve(id)
            .andThenTo(permission -> permission.enforceReplacement(data.constraints.stream().map(ConstraintData::toConstraint).findFirst().get()))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(PermissionData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> forget(final String id, final PermissionData data) {
    return resolve(id)
            .andThenTo(permission -> permission.forget(data.constraints.stream().map(ConstraintData::toConstraint).findFirst().get()))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(PermissionData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> changeDescription(final String id, final PermissionData data) {
    return resolve(id)
            .andThenTo(permission -> permission.changeDescription(data.description, data.tenantId))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(PermissionData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> permissions() {
    return $queries.permissions()
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> permissionOf(final String id) {
    return $queries.permissionOf(id)
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  @Override
  public Resource<?> routes() {
     return resource("PermissionResource",
        io.vlingo.xoom.http.resource.ResourceBuilder.post("/tenants/{tenantId}/permissions")
            .body(PermissionData.class)
            .handle(this::provisionPermission),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/permissions/{permissionName}/constraints")
            .param(String.class)
            .body(PermissionData.class)
            .handle(this::enforce),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/permissions/{permissionName}/constraints/{constraintName}")
            .param(String.class)
            .body(PermissionData.class)
            .handle(this::enforceReplacement),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/permissions/{permissionName}/constraints/{constraintName}")
            .param(String.class)
            .param(PermissionData.class)
            .handle(this::forget),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/permissions/{permissionName}/description")
            .param(String.class)
            .body(PermissionData.class)
            .handle(this::changeDescription),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/permissions")
            .handle(this::permissions),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/permissions/{id}")
            .param(String.class)
            .handle(this::permissionOf)
     );
  }

  @Override
  protected ContentType contentType() {
    return ContentType.of("application/json", "charset=UTF-8");
  }

  private String location(final String id) {
    return "/tenants/" + id;
  }

  private Completes<Permission> resolve(final String id) {
    final Address address = grid.addressFactory().from(id);
    return grid.actorOf(Permission.class, address, Definition.has(PermissionEntity.class, Definition.parameters(id)));
  }

}
