package io.vlingo.auth.infra.resource;

import java.util.List;
import java.util.function.Consumer;

import io.vlingo.http.Context;
import io.vlingo.http.resource.Action;
import io.vlingo.http.resource.Action.MappedParameters;
import io.vlingo.http.resource.ConfigurationResource;
import io.vlingo.http.resource.ResourceHandler;

public class TenantResourceDispatcher extends ConfigurationResource<TenantResource> {

  public TenantResourceDispatcher(
          final String name,
          final Class<? extends ResourceHandler> resourceHandlerClass,
          final int handlerPoolSize,
          final List<Action> actions) {
    super(name, resourceHandlerClass, handlerPoolSize, actions);
  }

  @Override
  public void dispatchToHandlerWith(final Context context, final MappedParameters mappedParameters) {
    Consumer<TenantResource> consumer = null;

    try {
      switch (mappedParameters.actionId) {
      case 0: // POST /tenants subscribeFor(body:io.vlingo.auth.infra.resource.TenantData tenantData)
        consumer = (handler) -> handler.subscribeFor((io.vlingo.auth.infra.resource.TenantData) mappedParameters.mapped.get(0).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 1: // PATCH /tenants/{tenantId}/activate activate(String tenantId)
        consumer = (handler) -> handler.activate((String) mappedParameters.mapped.get(0).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 2: // PATCH /tenants/{tenantId}/deactivate deactivate(String tenantId)
        consumer = (handler) -> handler.deactivate((String) mappedParameters.mapped.get(0).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 3: // PATCH /tenants/{tenantId}/description changeDescription(String tenantId, body:java.lang.String description)
        consumer = (handler) -> handler.changeDescription((String) mappedParameters.mapped.get(0).value, (java.lang.String) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 4: // PATCH /tenants/{tenantId}/name changeName(String tenantId, body:java.lang.String name)
        consumer = (handler) -> handler.changeName((String) mappedParameters.mapped.get(0).value, (java.lang.String) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 5: // POST /tenants/{tenantId}/groups provisionGroup(String tenantId, body:io.vlingo.auth.infra.resource.GroupData groupData)
        consumer = (handler) -> handler.provisionGroup((String) mappedParameters.mapped.get(0).value, (io.vlingo.auth.infra.resource.GroupData) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 6: // POST /tenants/{tenantId}/permissions provisionPermission(String tenantId, body:io.vlingo.auth.infra.resource.PermissionData permissionData)
        consumer = (handler) -> handler.provisionPermission((String) mappedParameters.mapped.get(0).value, (io.vlingo.auth.infra.resource.PermissionData) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 7: // POST /tenants/{tenantId}/roles provisionRole(String tenantId, body:io.vlingo.auth.infra.resource.RoleData roleData)
        consumer = (handler) -> handler.provisionRole((String) mappedParameters.mapped.get(0).value, (io.vlingo.auth.infra.resource.RoleData) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 8: // POST /tenants/{tenantId}/users registerUser(String tenantId, body:io.vlingo.auth.infra.resource.UserRegistrationData userData)
        consumer = (handler) -> handler.registerUser((String) mappedParameters.mapped.get(0).value, (io.vlingo.auth.infra.resource.UserRegistrationData) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 9: // GET /tenants/{tenantId} queryTenant(String tenantId)
        consumer = (handler) -> handler.queryTenant((String) mappedParameters.mapped.get(0).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 10: // GET /tenants/{tenantId}/groups queryGroups(String tenantId)
        consumer = (handler) -> handler.queryGroups((String) mappedParameters.mapped.get(0).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 11: // GET /tenants/{tenantId}/permissions queryPermissions(String tenantId)
        consumer = (handler) -> handler.queryPermissions((String) mappedParameters.mapped.get(0).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 12: // GET /tenants/{tenantId}/roles queryRoles(String tenantId)
        consumer = (handler) -> handler.queryRoles((String) mappedParameters.mapped.get(0).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 13: // GET /tenants/{tenantId}/users queryUsers(String tenantId)
        consumer = (handler) -> handler.queryUsers((String) mappedParameters.mapped.get(0).value);
        pooledHandler().handleFor(context, consumer);
        break;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Action mismatch: Request: " + context.request + "Parameters: " + mappedParameters);
    }
  }
}
