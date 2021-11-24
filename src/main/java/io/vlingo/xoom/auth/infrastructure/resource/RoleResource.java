package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.infrastructure.persistence.*;
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
  private final RoleQueries $roleQueries;
  private final PermissionQueries $permissionQueries;
  private final GroupQueries $groupQueries;
  private final UserQueries $userQueries;

  public RoleResource(final Grid grid) {
      super(grid.world().stage());
      this.grid = grid;
      this.$roleQueries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).roleQueries;
      this.$permissionQueries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).permissionQueries;
      this.$groupQueries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).groupQueries;
      this.$userQueries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).userQueries;
  }

  public Completes<Response> provisionRole(final RoleData data) {
    return create(data.tenantId, data.name)
      .provisionRole(data.name, data.description)
      .andThenTo(state -> Completes.withSuccess(entityResponseOf(Created, ResponseHeader.headers(ResponseHeader.of(Location, location(state.roleId))), serialized(RoleView.from(state))))
      .otherwise(arg -> Response.of(NotFound))
      .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())));
  }

  public Completes<Response> changeDescription(final String tenantId, final String roleName, final RoleData data) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.changeDescription(data.description))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleView.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> assignGroup(final String tenantId, final String roleName, final GroupData groupData) {
    final GroupId groupId = GroupId.from(TenantId.from(tenantId), groupData.name);
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.assignGroup(groupId))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, roleGroupLocation(state.roleId, groupId))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> unassignGroup(final String tenantId, final String roleName, final String groupName) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.unassignGroup(GroupId.from(TenantId.from(tenantId), groupName)))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleView.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> assignUser(final String tenantId, final String roleName, final UserRegistrationData userData) {
    final UserId userId = UserId.from(TenantId.from(tenantId), userData.username);
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.assignUser(userId))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, roleUserLocation(state.roleId, userId))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> unassignUser(final String tenantId, final String roleName, final String username) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.unassignUser(UserId.from(TenantId.from(tenantId), username)))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleView.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> attach(final String tenantId, final String roleName, final PermissionData permissionData) {
    final PermissionId permissionId = PermissionId.from(TenantId.from(tenantId), permissionData.name);
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.attach(permissionId))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, rolePermissionLocation(state.roleId, permissionId))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> detach(final String tenantId, final String roleName, final String permissionName) {
    return resolve(tenantId, roleName)
            .andThenTo(role -> role.detach(PermissionId.from(TenantId.from(tenantId), permissionName)))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(RoleView.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> roles() {
    return $roleQueries.roles()
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> roleOf(final String tenantId, final String roleName) {
    return $roleQueries.roleOf(RoleId.from(TenantId.from(tenantId), roleName))
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> permissionOf(final String tenantIdString, final String roleName, final String permissionName) {
    final TenantId tenantId = TenantId.from(tenantIdString);
    final RoleId roleId = RoleId.from(tenantId, roleName);
    final PermissionId permissionId = PermissionId.from(tenantId, permissionName);
    return $permissionQueries.permissionOf(permissionId)
            .andThen(permissionView -> permissionView.isAttachedTo(roleId) ? permissionView : null)
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> groupOf(final String tenantId, final String roleName, final String groupName) {
    final RoleId roleId = RoleId.from(TenantId.from(tenantId), roleName);
    final GroupId groupId = GroupId.from(roleId.tenantId, groupName);
    return $groupQueries.groupOf(groupId)
            .andThen(groupView -> groupView.isInRole(roleId) ? groupView : null)
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> userOf(final String tenantId, final String roleName, final String username) {
    final RoleId roleId = RoleId.from(TenantId.from(tenantId), roleName);
    final UserId userId = UserId.from(roleId.tenantId, username);
    return $userQueries.userOf(userId)
            .andThen(userView -> userView.isInRole(roleId) ? userView : null)
            .andThenTo(userView -> Completes.withSuccess(entityResponseOf(Ok, serialized(MinimalUserData.from(userView)))))
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
            .body(GroupData.class)
            .handle(this::assignGroup),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/roles/{roleName}/groups/{groupName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::unassignGroup),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/roles/{roleName}/groups/{groupName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::groupOf),
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/roles/{roleName}/users")
            .param(String.class)
            .param(String.class)
            .body(UserRegistrationData.class)
            .handle(this::assignUser),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/roles/{roleName}/users/{username}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::unassignUser),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/roles/{roleName}/users/{username}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::userOf),
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/roles/{roleName}/permissions")
            .param(String.class)
            .param(String.class)
            .body(PermissionData.class)
            .handle(this::attach),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/roles/{roleName}/permissions/{permissionName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::detach),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/roles/{roleName}/permissions/{permissionName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .handle(this::permissionOf),
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

  private String roleGroupLocation(final RoleId roleId, final GroupId groupId) {
    return String.format("/tenants/%s/roles/%s/groups/%s", roleId.tenantId.id, roleId.roleName, groupId.groupName);
  }

  private String roleUserLocation(final RoleId roleId, final UserId userId) {
    return String.format("/tenants/%s/roles/%s/users/%s", roleId.tenantId.id, roleId.roleName, userId.username);
  }

  private String rolePermissionLocation(final RoleId roleId, final PermissionId permissionId) {
    return String.format("/tenants/%s/roles/%s/permissions/%s", roleId.tenantId.id, roleId.roleName, permissionId.permissionName);
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
