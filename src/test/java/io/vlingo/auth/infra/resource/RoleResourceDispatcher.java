package io.vlingo.auth.infra.resource;

import java.util.List;
import java.util.function.Consumer;

import io.vlingo.http.Context;
import io.vlingo.http.resource.Action;
import io.vlingo.http.resource.Action.MappedParameters;
import io.vlingo.http.resource.ConfigurationResource;
import io.vlingo.http.resource.ResourceHandler;

public class RoleResourceDispatcher extends ConfigurationResource<RoleResource> {

  public RoleResourceDispatcher(
          final String name,
          final Class<? extends ResourceHandler> resourceHandlerClass,
          final int handlerPoolSize,
          final List<Action> actions) {
    super(name, resourceHandlerClass, handlerPoolSize, actions);
  }

  @Override
  public void dispatchToHandlerWith(final Context context, final MappedParameters mappedParameters) {
    Consumer<RoleResource> consumer = null;

    try {
      switch (mappedParameters.actionId) {
      case 0: // PATCH /tenants/{tenantId}/roles/{roleName}/description changeDescription(String tenantId, String roleName, body:java.lang.String description)
        consumer = (handler) -> handler.changeDescription((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (java.lang.String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 1: // PUT /tenants/{tenantId}/roles/{roleName}/groups assignGroup(String tenantId, String roleName, body:java.lang.String groupName)
        consumer = (handler) -> handler.assignGroup((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (java.lang.String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 2: // DELETE /tenants/{tenantId}/roles/{roleName}/groups/{groupName} unassignGroup(String tenantId, String roleName, String groupName)
        consumer = (handler) -> handler.unassignGroup((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 3: // PUT /tenants/{tenantId}/roles/{roleName}/users assignUser(String tenantId, String roleName, body:java.lang.String username)
        consumer = (handler) -> handler.assignUser((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (java.lang.String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 4: // DELETE /tenants/{tenantId}/roles/{roleName}/users/{username} unassignUser(String tenantId, String roleName, String username)
        consumer = (handler) -> handler.unassignUser((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 5: // PUT /tenants/{tenantId}/roles/{roleName}/permissions attach(String tenantId, String roleName, body:java.lang.String permissionName)
        consumer = (handler) -> handler.attach((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (java.lang.String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 6: // DELETE /tenants/{tenantId}/roles/{roleName}/permissions/{permissionName} detach(String tenantId, String roleName, String permissionName)
        consumer = (handler) -> handler.detach((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 7: // GET /tenants/{tenantId}/roles/{roleName} queryRole(String tenantId, String roleName)
        consumer = (handler) -> handler.queryRole((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 8: // GET /tenants/{tenantId}/roles/{roleName}/permissions/{permissionName} queryPermission(String tenantId, String roleName, String permissionName)
        consumer = (handler) -> handler.queryPermission((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 9: // GET /tenants/{tenantId}/roles/{roleName}/groups/{groupName} queryGroup(String tenantId, String roleName, String groupName)
        consumer = (handler) -> handler.queryGroup((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 10: // GET /tenants/{tenantId}/roles/{roleName}/users/{username} queryUser(String tenantId, String roleName, String username)
        consumer = (handler) -> handler.queryUser((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Action mismatch: Request: " + context.request + "Parameters: " + mappedParameters);
    }
  }
}
