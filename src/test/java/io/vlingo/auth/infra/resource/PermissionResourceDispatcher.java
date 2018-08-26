package io.vlingo.auth.infra.resource;

import java.util.List;
import java.util.function.Consumer;

import io.vlingo.http.Context;
import io.vlingo.http.resource.Action;
import io.vlingo.http.resource.Action.MappedParameters;
import io.vlingo.http.resource.ConfigurationResource;
import io.vlingo.http.resource.ResourceHandler;

public class PermissionResourceDispatcher extends ConfigurationResource<PermissionResource> {

  public PermissionResourceDispatcher(
          final String name,
          final Class<? extends ResourceHandler> resourceHandlerClass,
          final int handlerPoolSize,
          final List<Action> actions) {
    super(name, resourceHandlerClass, handlerPoolSize, actions);
  }

  @Override
  public void dispatchToHandlerWith(final Context context, final MappedParameters mappedParameters) {
    Consumer<PermissionResource> consumer = null;

    try {
      switch (mappedParameters.actionId) {
      case 0: // PATCH /tenants/{tenantId}/permissions/{permissionName}/constraints enforce(String tenantId, String permissionName, body:io.vlingo.auth.infra.resource.ConstraintData constraintData)
        consumer = (handler) -> handler.enforce((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (io.vlingo.auth.infra.resource.ConstraintData) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 1: // PATCH /tenants/{tenantId}/permissions/{permissionName}/constraints/{constraintName} enforceReplacement(String tenantId, String permissionName, String constraintName, body:io.vlingo.auth.infra.resource.ConstraintData constraintData)
        consumer = (handler) -> handler.enforceReplacement((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value, (io.vlingo.auth.infra.resource.ConstraintData) mappedParameters.mapped.get(3).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 2: // DELETE /tenants/{tenantId}/permissions/{permissionName}/constraints/{constraintName} forget(String tenantId, String permissionName, String constraintName)
        consumer = (handler) -> handler.forget((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 3: // PATCH /tenants/{tenantId}/permissions/{permissionName}/description changeDescription(String tenantId, String permissionName, body:java.lang.String description)
        consumer = (handler) -> handler.changeDescription((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (java.lang.String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        break;
      case 4: // GET /tenants/{tenantId}/permissions/{permissionName} queryPermission(String tenantId, String permissionName)
        consumer = (handler) -> handler.queryPermission((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        break;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Action mismatch: Request: " + context.request + "Parameters: " + mappedParameters);
    }
  }
}
