package io.vlingo.auth.infra.resource;

import java.util.List;
import java.util.function.Consumer;

import io.vlingo.http.Context;
import io.vlingo.http.resource.Action;
import io.vlingo.http.resource.Action.MappedParameters;
import io.vlingo.http.resource.ConfigurationResource;
import io.vlingo.http.resource.ResourceHandler;

public class GroupResourceDispatcher extends ConfigurationResource<GroupResource> {

  public GroupResourceDispatcher(
          final String name,
          final Class<? extends ResourceHandler> resourceHandlerClass,
          final int handlerPoolSize,
          final List<Action> actions) {
    super(name, resourceHandlerClass, handlerPoolSize, actions);
  }

  @Override
  public void dispatchToHandlerWith(final Context context, final MappedParameters mappedParameters) {
    Consumer<GroupResource> consumer = null;

    try {
      switch (mappedParameters.actionId) {
      case 0: // PATCH /tenants/{tenantId}/groups/{groupName}/description changeDescription(String tenantId, String groupName, body:java.lang.String description)
        consumer = (handler) -> handler.changeDescription((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (java.lang.String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("GP");
        break;
      case 1: // PUT /tenants/{tenantId}/groups/{groupName}/groups assignGroup(String tenantId, String groupName, body:java.lang.String groupName)
        consumer = (handler) -> handler.assignGroup((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (java.lang.String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("GU1");
        break;
      case 2: // DELETE /tenants/{tenantId}/groups/{groupName}/groups/{innerGroupName} unassignGroup(String tenantId, String groupName, String innerGroupName)
        consumer = (handler) -> handler.unassignGroup((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("GD1");
        break;
      case 3: // PUT /tenants/{tenantId}/groups/{groupName}/users assignUser(String tenantId, String groupName, body:java.lang.String username)
        consumer = (handler) -> handler.assignUser((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (java.lang.String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("GU2");
        break;
      case 4: // DELETE /tenants/{tenantId}/groups/{groupName}/users/{username} unassignUser(String tenantId, String groupName, String username)
        consumer = (handler) -> handler.unassignUser((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("GD2");
        break;
      case 5: // GET /tenants/{tenantId}/groups/{groupName} queryGroup(String tenantId, String groupName)
        consumer = (handler) -> handler.queryGroup((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("GG1");
        break;
      case 6: // GET /tenants/{tenantId}/groups/{groupName}/groups/{innerGroupName} queryInnerGroup(String tenantId, String groupName, String innerGroupName)
        consumer = (handler) -> handler.queryInnerGroup((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("GG2");
        break;
      case 7: // GET /tenants/{tenantId}/groups/{groupName}/permissions/{permissionName} queryPermission(String tenantId, String groupName, String permissionName)
        consumer = (handler) -> handler.queryPermission((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("GG3");
        break;
      case 8: // GET /tenants/{tenantId}/groups/{groupName}/roles/{roleName} queryRole(String tenantId, String groupName, String roleName)
        consumer = (handler) -> handler.queryRole((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("GG4");
        break;
      case 9: // GET /tenants/{tenantId}/groups/{groupName}/users/{username} queryUser(String tenantId, String groupName, String username)
        consumer = (handler) -> handler.queryUser((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("GG5");
        break;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Action mismatch: Request: " + context.request + "Parameters: " + mappedParameters);
    }
  }
}
