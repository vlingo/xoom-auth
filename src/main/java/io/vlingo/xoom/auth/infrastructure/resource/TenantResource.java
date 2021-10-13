package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.ContentType;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.auth.infrastructure.persistence.QueryModelStateStoreProvider;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.model.tenant.TenantEntity;
import io.vlingo.xoom.auth.infrastructure.persistence.TenantQueries;
import io.vlingo.xoom.auth.model.tenant.Tenant;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;

/**
 * See <a href="https://docs.vlingo.io/xoom-turbo/xoom-annotations#resourcehandlers">@ResourceHandlers</a>
 */
public class TenantResource extends DynamicResourceHandler {
  private final Grid grid;
  private final TenantQueries $queries;

  public TenantResource(final Grid grid) {
      super(grid.world().stage());
      this.grid = grid;
      this.$queries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).tenantQueries;
  }

  public Completes<Response> subscribeFor(final TenantData data) {
    return create()
      .subscribeFor(data.name, data.description, data.active)
      .andThenTo(state -> Completes.withSuccess(entityResponseOf(Created, ResponseHeader.headers(ResponseHeader.of(Location, location(state.tenantId))), serialized(TenantData.from(state))))
      .otherwise(arg -> Response.of(NotFound))
      .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())));
  }

  public Completes<Response> activate(final String id) {
    return resolve(id)
            .andThenTo(tenant -> tenant.activate())
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(TenantData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> deactivate(final String id) {
    return resolve(id)
            .andThenTo(tenant -> tenant.deactivate())
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(TenantData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> changeDescription(final String id, final TenantData data) {
    return resolve(id)
            .andThenTo(tenant -> tenant.changeDescription(data.description))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(TenantData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> changeName(final String id, final TenantData data) {
    return resolve(id)
            .andThenTo(tenant -> tenant.changeName(data.name))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(TenantData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> tenants() {
    return $queries.tenants()
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> tenantOf(final String id) {
    return $queries.tenantOf(TenantId.from(id))
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  @Override
  public Resource<?> routes() {
     return resource("TenantResource",
        io.vlingo.xoom.http.resource.ResourceBuilder.post("/tenants")
            .body(TenantData.class)
            .handle(this::subscribeFor),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{id}/activate")
            .param(String.class)
            .handle(this::activate),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{id}/deactivate")
            .param(String.class)
            .handle(this::deactivate),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{id}/description")
            .param(String.class)
            .body(TenantData.class)
            .handle(this::changeDescription),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{id}/name")
            .param(String.class)
            .body(TenantData.class)
            .handle(this::changeName),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants")
            .handle(this::tenants),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{id}")
            .param(String.class)
            .handle(this::tenantOf)
     );
  }

  @Override
  protected ContentType contentType() {
    return ContentType.of("application/json", "charset=UTF-8");
  }

  private String location(final TenantId tenantId) {
    return String.format("/tenants/%s", tenantId.id);
  }

  private Completes<Tenant> resolve(final String id) {
    final TenantId tenantId = TenantId.from(id);
    final Address address = grid.addressFactory().from(tenantId.idString());
    return grid.actorOf(Tenant.class, address, Definition.has(TenantEntity.class, Definition.parameters(tenantId)));
  }

  private Tenant create() {
    final Address address = grid.addressFactory().uniquePrefixedWith("g-");
    final TenantId tenantId = TenantId.from(address.idString());
    return grid.actorFor(Tenant.class, Definition.has(TenantEntity.class, Definition.parameters(tenantId)), address);
  }
}
