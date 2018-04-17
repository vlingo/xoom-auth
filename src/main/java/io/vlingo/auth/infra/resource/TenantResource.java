// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.infra.resource;

import static io.vlingo.http.Response.Created;
import static io.vlingo.http.Response.NotFound;
import static io.vlingo.http.Response.Ok;
import static io.vlingo.http.ResponseHeader.Location;
import static io.vlingo.http.ResponseHeader.headers;
import static io.vlingo.http.ResponseHeader.of;
import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

import io.vlingo.auth.infra.persistence.RepositoryProvider;
import io.vlingo.auth.model.Tenant;
import io.vlingo.auth.model.TenantId;
import io.vlingo.auth.model.TenantRepository;
import io.vlingo.http.Response;
import io.vlingo.http.resource.ResourceHandler;

public class TenantResource extends ResourceHandler {
  private final TenantRepository repository = RepositoryProvider.tenantRepository();

  public TenantResource() { }

  public void subscribeFor(final TenantData tenantData) {
    final Tenant tenant = Tenant.subscribeFor(tenantData.name, tenantData.description, tenantData.active);

    repository.save(tenant);

    completes().with(Response.of(Created, headers(of(Location, location(tenant.tenantId().value))), serialized(TenantData.from(tenant))));
  }

  public void activate(final String tenantId) {
    final Tenant tenant = repository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      tenant.activate();
      repository.save(tenant);
      TenantData data = TenantData.from(tenant);
      String ser = serialized(data);
      completes().with(Response.of(Ok, ser));
    }
  }

  public void deactivate(final String tenantId) {
    final Tenant tenant = repository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      tenant.deactivate();
      repository.save(tenant);
      completes().with(Response.of(Ok, serialized(TenantData.from(tenant))));
    }
  }

  public void changeDescription(final String tenantId, final String description) {
    final Tenant tenant = repository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      tenant.changeDescription(description);
      repository.save(tenant);
      completes().with(Response.of(Ok, serialized(TenantData.from(tenant))));
    }
  }

  public void changeName(final String tenantId, final String name) {
    final Tenant tenant = repository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      tenant.changeName(name);
      repository.save(tenant);
      completes().with(Response.of(Ok, serialized(TenantData.from(tenant))));
    }
  }

  public void queryTenant(final String tenantId) {
    final Tenant tenant = repository.tenantOf(TenantId.fromExisting(tenantId));
    if (tenant.doesNotExist()) {
      completes().with(Response.of(NotFound, location(tenantId)));
    } else {
      completes().with(Response.of(Ok, serialized(TenantData.from(tenant))));
    }
  }

  private String location(final String tenantId) {
    return "/tenants/" + tenantId;
  }
}
