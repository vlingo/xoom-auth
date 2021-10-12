package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.auth.infrastructure.GroupData;
import io.vlingo.xoom.auth.infrastructure.persistence.GroupQueries;
import io.vlingo.xoom.auth.infrastructure.persistence.QueryModelStateStoreProvider;
import io.vlingo.xoom.auth.model.group.Group;
import io.vlingo.xoom.auth.model.group.GroupEntity;
import io.vlingo.xoom.auth.model.group.GroupId;
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
public class GroupResource extends DynamicResourceHandler {
  private final Grid grid;
  private final GroupQueries $queries;

  public GroupResource(final Grid grid) {
      super(grid.world().stage());
      this.grid = grid;
      this.$queries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).groupQueries;
  }

  public Completes<Response> provisionGroup(final GroupData data) {
    return create(data.tenantId, data.name)
      .provisionGroup(data.name, data.description)
      .andThenTo(state -> Completes.withSuccess(entityResponseOf(Created, ResponseHeader.headers(ResponseHeader.of(Location, location(state.id))), serialized(GroupData.from(state))))
      .otherwise(arg -> Response.of(NotFound))
      .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())));
  }

  public Completes<Response> changeDescription(final String tenantId, final String groupName, final GroupData data) {
    return resolve(tenantId, groupName)
            .andThenTo(group -> group.changeDescription(data.description))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(GroupData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> assignGroup(final String tenantId, final String groupName, final String innerGroupName, final GroupData data) {
    return resolve(tenantId, groupName)
            .andThenTo(group -> group.assignGroup(GroupId.from(tenantId, innerGroupName)))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(GroupData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> unassignGroup(final String tenantId, final String groupName, final String innerGroupName, final GroupData data) {
    return resolve(tenantId, groupName)
            .andThenTo(group -> group.unassignGroup(GroupId.from(tenantId, innerGroupName)))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(GroupData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> assignUser(final String tenantId, final String groupName, final String username, final GroupData data) {
    return resolve(tenantId, groupName)
            .andThenTo(group -> group.assignUser(data.tenantId))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(GroupData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> unassignUser(final String tenantId, final String groupName, final String username, final GroupData data) {
    return resolve(tenantId, groupName)
            .andThenTo(group -> group.unassignUser(data.tenantId))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(GroupData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> groups() {
    return $queries.groups()
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> groupOf(final String tenantId, final String groupName) {
    return $queries.groupOf(GroupId.from(tenantId, groupName))
            .andThenTo(data -> Completes.withSuccess(entityResponseOf(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  @Override
  public Resource<?> routes() {
     return resource("GroupResource",
        io.vlingo.xoom.http.resource.ResourceBuilder.post("/tenants/{tenantId}/groups")
            .body(GroupData.class)
            .handle(this::provisionGroup),
        io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/groups/{groupName}/description")
            .param(String.class)
            .param(String.class)
            .body(GroupData.class)
            .handle(this::changeDescription),
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/groups/{groupName}/groups/{innerGroupName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .body(GroupData.class)
            .handle(this::assignGroup),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/groups/{groupName}/groups/{innerGroupName}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .body(GroupData.class)
            .handle(this::unassignGroup),
        io.vlingo.xoom.http.resource.ResourceBuilder.put("/tenants/{tenantId}/groups/{groupName}/users/{username}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .body(GroupData.class)
            .handle(this::assignUser),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/groups/{groupName}/users/{username}")
            .param(String.class)
            .param(String.class)
            .param(String.class)
            .body(GroupData.class)
            .handle(this::unassignUser),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/groups")
            .handle(this::groups),
        io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/groups/{groupName}")
            .param(String.class)
            .param(String.class)
            .handle(this::groupOf)
     );
  }

  @Override
  protected ContentType contentType() {
    return ContentType.of("application/json", "charset=UTF-8");
  }

  private String location(final GroupId groupId) {
    return String.format("/tenants/%s/groups/%s", groupId.tenantId, groupId.groupName);
  }

  private Completes<Group> resolve(final String tenantId, final String groupName) {
    final GroupId groupId = GroupId.from(tenantId, groupName);
    final Address address = new GroupAddress(groupId);
    return grid.actorOf(Group.class, address, Definition.has(GroupEntity.class, Definition.parameters(groupId)));
  }

  private Group create(final String tenantId, final String groupName) {
    final GroupId groupId = GroupId.from(tenantId, groupName);
    final Address address = new GroupAddress(groupId);
    return grid.actorFor(Group.class, Definition.has(GroupEntity.class, Definition.parameters(groupId)), address);
  }

  private class GroupAddress extends ComplexAddress<GroupId> {
    public GroupAddress(final GroupId groupId) {
      super(groupId, (id) -> id.idString());
    }
  }
}
