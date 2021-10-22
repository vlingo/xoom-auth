package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.auth.infrastructure.RoleData;
import io.vlingo.xoom.auth.infrastructure.persistence.QueryModelStateStoreProvider;
import io.vlingo.xoom.auth.infrastructure.persistence.RoleQueries;
import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.auth.model.role.Role;
import io.vlingo.xoom.auth.model.role.RoleEntity;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.ContentType;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.turbo.ComponentRegistry;

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
    return create(data.tenantId, data.name)
      .provisionRole(data.name, data.description)
      .andThenTo(state -> Completes.withSuccess(entityResponseOf(Created, ResponseHeader.headers(ResponseHeader.of(Location, location(state.roleId))), serialized(RoleData.from(state))))
      .otherwise(arg -> Response.of(NotFound))
      .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())));
  }

  public Completes<Response> changeDescription(final String tenantId, final String roleName, final RoleData data) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.changeDescription(data.description))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> assignGroup(final String tenantId, final String roleName, final String groupName) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.assignGroup(GroupId.from(TenantId.from(tenantId), groupName)))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> unassignGroup(final String tenantId, final String roleName, final String groupName) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.unassignGroup(GroupId.from(TenantId.from(tenantId), groupName)))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> assignUser(final String tenantId, final String roleName, final String username) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.assignUser(UserId.from(TenantId.from(tenantId), username)))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> unassignUser(final String tenantId, final String roleName, final String username) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.unassignUser(UserId.from(TenantId.from(tenantId), username)))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> attach(final String tenantId, final String roleName, final String permissionName) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.attach(PermissionId.from(TenantId.from(tenantId), permissionName)))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> detach(final String tenantId, final String roleName, final String permissionName) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.detach(PermissionId.from(TenantId.from(tenantId), permissionName)))
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

  public Completes<Response> roleOf(final String tenantId, final String roleName) {
    return $queries.roleOf(RoleId.from(TenantId.from(tenantId), roleName))
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
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/roles/{roleName}/groups/{groupName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::assignGroup),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/roles/{roleName}/groups/{groupName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::unassignGroup),
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/roles/{roleName}/users/{username}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::assignUser),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/roles/{roleName}/users/{username}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::unassignUser),
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/roles/{roleName}/permissions/{permissionName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::attach),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/roles/{roleName}/permissions/{permissionName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::detach),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/roles")
            .handle(this::roles),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/roles/{roleName}")
            .param(String.class)
            .param(String.class)
            .handle(this::roleOf)
     );
  }

  @Override
  protected ContentType contentType() {
    return ContentType.of("application/json", "charset=UTF-8");
  }

  private String location(final RoleId roleId) {
    return String.format("/tenants/%s/roles/%s", roleId.tenantId.id, roleId.roleName);
  }

  private Completes<Role> resolve(final String tenantId, String roleName) {
    final RoleId roleId = RoleId.from(TenantId.from(tenantId), roleName);
    final Address address = new RoleAddress(roleId);
    return grid.actorOf(Role.class, address, Definition.has(RoleEntity.class, Definition.parameters(roleId)));
  }

  private Role create(String tenantId, String roleName) {
    final RoleId roleId = RoleId.from(TenantId.from(tenantId), roleName);
    final Address address = new RoleAddress(roleId);
    return grid.actorFor(Role.class, Definition.has(RoleEntity.class, Definition.parameters(roleId)), address);
  }

  private class RoleAddress extends ComplexAddress<RoleId> {
    public RoleAddress(final RoleId roleId) {
      super(roleId, (id) -> id.idString());
    }
  }
}
