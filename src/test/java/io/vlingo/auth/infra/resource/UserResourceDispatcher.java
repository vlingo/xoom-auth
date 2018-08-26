package io.vlingo.auth.infra.resource;

import java.util.List;
import java.util.function.Consumer;

import io.vlingo.http.Context;
import io.vlingo.http.resource.Action;
import io.vlingo.http.resource.Action.MappedParameters;
import io.vlingo.http.resource.ConfigurationResource;
import io.vlingo.http.resource.ResourceHandler;

public class UserResourceDispatcher extends ConfigurationResource<UserResource> {

  public UserResourceDispatcher(
          final String name,
          final Class<? extends ResourceHandler> resourceHandlerClass,
          final int handlerPoolSize,
          final List<Action> actions) {
    super(name, resourceHandlerClass, handlerPoolSize, actions);
  }

  @Override
  public void dispatchToHandlerWith(final Context context, final MappedParameters mappedParameters) {
    Consumer<UserResource> consumer = null;

    try {
      switch (mappedParameters.actionId) {
      case 0: // PATCH /tenants/{tenantId}/users/{username}/activate activate(String tenantId, String username)
        consumer = (handler) -> handler.activate((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("UP1");
        break;
      case 1: // PATCH /tenants/{tenantId}/users/{username}/deactivate deactivate(String tenantId, String username)
        consumer = (handler) -> handler.deactivate((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("UT");
        break;
      case 2: // PUT /tenants/{tenantId}/users/{username}/credentials addCredential(String tenantId, String username, body:io.vlingo.auth.infra.resource.CredentialData credentialData)
        consumer = (handler) -> handler.addCredential((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (io.vlingo.auth.infra.resource.CredentialData) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("UU");
        break;
      case 3: // DELETE /tenants/{tenantId}/users/{username}/credentials/{authority} removeCredential(String tenantId, String username, String authority)
        consumer = (handler) -> handler.removeCredential((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("UD");
        break;
      case 4: // PATCH /tenants/{tenantId}/users/{username}/credentials/{authority} replaceCredential(String tenantId, String username, String authority, body:io.vlingo.auth.infra.resource.CredentialData credentialData)
        consumer = (handler) -> handler.replaceCredential((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value, (io.vlingo.auth.infra.resource.CredentialData) mappedParameters.mapped.get(3).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("UP2");
        break;
      case 5: // PATCH /tenants/{tenantId}/users/{username}/profile profile(String tenantId, String username, body:io.vlingo.auth.infra.resource.ProfileData profileData)
        consumer = (handler) -> handler.profile((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (io.vlingo.auth.infra.resource.ProfileData) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("UP3");
        break;
      case 6: // GET /tenants/{tenantId}/users/{username} queryUser(String tenantId, String username)
        consumer = (handler) -> handler.queryUser((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("UG1");
        break;
      case 7: // GET /tenants/{tenantId}/users/{username}/permissions/{permissionName} queryPermission(String tenantId, String username, String permissionName)
        consumer = (handler) -> handler.queryPermission((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("UG2");
        break;
      case 8: // GET /tenants/{tenantId}/users/{username}/roles/{roleName} queryRole(String tenantId, String username, String roleName)
        consumer = (handler) -> handler.queryRole((String) mappedParameters.mapped.get(0).value, (String) mappedParameters.mapped.get(1).value, (String) mappedParameters.mapped.get(2).value);
        pooledHandler().handleFor(context, consumer);
        System.out.print("UG3");
        break;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Action mismatch: Request: " + context.request + "Parameters: " + mappedParameters);
    }
  }
}
