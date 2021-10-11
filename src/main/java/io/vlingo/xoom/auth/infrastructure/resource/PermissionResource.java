package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.auth.infrastructure.ConstraintData;
import io.vlingo.xoom.auth.infrastructure.PermissionData;
import io.vlingo.xoom.auth.infrastructure.persistence.PermissionQueries;
import io.vlingo.xoom.auth.infrastructure.persistence.QueryModelStateStoreProvider;
import io.vlingo.xoom.auth.model.permission.Permission;
import io.vlingo.xoom.auth.model.permission.PermissionEntity;
import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.ContentType;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.turbo.ComponentRegistry;

import java.nio.CharBuffer;
import java.util.UUID;

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
    return create(data.tenantId, data.name)
      .provisionPermission(data.name, data.description)
      .andThenTo(state -> Completes.withSuccess(entityResponseOf(Created, ResponseHeader.headers(ResponseHeader.of(Location, location(state.id))), serialized(PermissionData.from(state))))
      .otherwise(arg -> Response.of(NotFound))
      .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())));
  }

  public Completes<Response> enforce(final String tenantId, final String permissionName, final PermissionData data) {
    return resolve(tenantId, permissionName)
            .andThenTo(permission -> permission.enforce(data.constraints.stream().map(ConstraintData::toConstraint).findFirst().get()))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(PermissionData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> enforceReplacement(final String tenantId, final String permissionName, final String constraintName, final PermissionData data) {
    return resolve(tenantId, permissionName)
            .andThenTo(permission -> permission.enforceReplacement(data.constraints.stream().map(ConstraintData::toConstraint).findFirst().get()))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(PermissionData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> forget(final String tenantId, final String permissionName, final String constraintName, final PermissionData data) {
    return resolve(tenantId, permissionName)
            .andThenTo(permission -> permission.forget(data.constraints.stream().map(ConstraintData::toConstraint).findFirst().get()))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(PermissionData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> changeDescription(final String tenantId, final String permissionName, final PermissionData data) {
    return resolve(tenantId, permissionName)
            .andThenTo(permission -> permission.changeDescription(data.description))
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

  public Completes<Response> permissionOf(final String tenantId, final String permissionName) {
    return $queries.permissionOf(PermissionId.from(tenantId, permissionName))
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
            .param(String.class)
            .body(PermissionData.class)
            .handle(this::enforce),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/permissions/{permissionName}/constraints/{constraintName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .body(PermissionData.class)
            .handle(this::enforceReplacement),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/permissions/{permissionName}/constraints/{constraintName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .body(PermissionData.class)
            .handle(this::forget),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/permissions/{permissionName}/description")
            .param(String.class)
            .param(String.class)
            .body(PermissionData.class)
            .handle(this::changeDescription),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/permissions")
            .handle(this::permissions),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/permissions/{permissionName}")
            .param(String.class)
            .param(String.class)
            .handle(this::permissionOf)
     );
  }

  @Override
  protected ContentType contentType() {
    return ContentType.of("application/json", "charset=UTF-8");
  }

  private String location(final PermissionId permissionId) {
    return String.format("/tenants/%s/permissions/%s", permissionId.tenantId, permissionId.permissionName);
  }

  private Completes<Permission> resolve(final String tenantId, final String permissionName) {
    final PermissionId permissionId = PermissionId.from(tenantId, permissionName);
    final Address address = new PermissionAddress(permissionId);
    return grid.actorOf(Permission.class, address, Definition.has(PermissionEntity.class, Definition.parameters(permissionId)));
  }

  private Permission create(final String tenantId, final String permissionName) {
    final PermissionId permissionId = PermissionId.from(tenantId, permissionName);
    final Address address = new PermissionAddress(permissionId);
    return grid.actorFor(Permission.class, Definition.has(PermissionEntity.class, Definition.parameters(permissionId)), address);
  }

  // @TODO replace this with something better thought out
  public class PermissionAddress implements Address {

    private final PermissionId permissionId;

    public PermissionAddress(final PermissionId permissionId) {
      this.permissionId = permissionId;
    }

    @Override
    public long id() {
      final long groupNamePart = Long.valueOf(CharBuffer.wrap(permissionId.permissionName.toCharArray()).chars()
              .mapToObj(c -> {
                if (Character.isUpperCase(c)) {
                  return String.valueOf(Math.abs(c - 'A' + 1));
                } else {
                  return String.valueOf(Math.abs(c - 'a' + 1));
                }
              })
              .reduce("", (subtotal, element) -> subtotal + element));
      final long tenantIdPart = UUID.fromString(permissionId.tenantId).getMostSignificantBits();
      return (tenantIdPart + groupNamePart) & Long.MAX_VALUE;
    }

    @Override
    public long idSequence() {
      return id();
    }

    @Override
    public String idSequenceString() {
      return idString();
    }

    @Override
    public String idString() {
      return String.valueOf(id());
    }

    @Override
    public <T> T idTyped() {
      return (T) idString();
    }

    @Override
    public String name() {
      return "group";
    }

    @Override
    public boolean isDistributable() {
      return true;
    }

    @Override
    public int compareTo(Address other) {
      if (other instanceof GroupResource.GroupAddress) {
        if (permissionId.equals(other)) {
          return 0;
        } else {
          return 1;
        }
      } else {
        return -1;
      }
    }

    @Override
    public boolean equals(final Object other) {
      if (other == null || other.getClass() != PermissionAddress.class) {
        return false;
      }
      return permissionId.equals(((PermissionAddress) other).permissionId);
    }

    @Override
    public int hashCode() {
      return permissionId.hashCode();
    }

    @Override
    public String toString() {
      return "Address[permissionId=" + permissionId + "]";
    }
  }
}
