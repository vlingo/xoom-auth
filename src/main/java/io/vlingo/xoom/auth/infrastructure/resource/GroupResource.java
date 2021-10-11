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
      .andThenTo(state -> Completes.withSuccess(entityResponseOf(Created, ResponseHeader.headers(ResponseHeader.of(Location, location(state.id.idString()))), serialized(GroupData.from(state))))
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

  public Completes<Response> assignUser(final String id, final GroupData data) {
    return resolve(id)
            .andThenTo(group -> group.assignUser(data.tenantId))
            .andThenTo(state -> Completes.withSuccess(entityResponseOf(Ok, serialized(GroupData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> unassignUser(final String id, final GroupData data) {
    return resolve(id)
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
            .body(GroupData.class)
            .handle(this::assignUser),
        io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/groups/{groupName}/users/{username}")
            .param(String.class)
            .param(GroupData.class)
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

  private String location(final String id) {
    return "/tenants/" + id;
  }

  private Completes<Group> resolve(final String id) {
    final Address address = grid.addressFactory().from(id);
    return grid.actorOf(Group.class, address, Definition.has(GroupEntity.class, Definition.parameters(id)));
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

  // @TODO replace this with something better thought out
  public class GroupAddress implements Address {

    private final GroupId groupId;

    public GroupAddress(final GroupId groupId) {
      this.groupId = groupId;
    }

    @Override
    public long id() {
      final long groupNamePart = Long.valueOf(CharBuffer.wrap(groupId.groupName.toCharArray()).chars()
              .mapToObj(c -> {
                if (Character.isUpperCase(c)) {
                  return String.valueOf(Math.abs(c - 'A' + 1));
                } else {
                  return String.valueOf(Math.abs(c - 'a' + 1));
                }
              })
              .reduce("", (subtotal, element) -> subtotal + element));
      final long tenantIdPart = UUID.fromString(groupId.tenantId).getMostSignificantBits();
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
      if (other instanceof GroupAddress) {
        if (groupId.equals(other)) {
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
      if (other == null || other.getClass() != GroupAddress.class) {
        return false;
      }
      return groupId.equals(((GroupAddress) other).groupId);
    }

    @Override
    public int hashCode() {
      return groupId.hashCode();
    }

    @Override
    public String toString() {
      return "Address[groupId=" + groupId + "]";
    }
  }
}
