// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infra.resource;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.Created;
import static io.vlingo.xoom.http.Response.Status.NotFound;
import static io.vlingo.xoom.http.Response.Status.Ok;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static io.vlingo.xoom.http.ResponseHeader.headers;
import static io.vlingo.xoom.http.ResponseHeader.of;

import io.vlingo.xoom.auth.infra.persistence.RepositoryProvider;
import io.vlingo.xoom.auth.model.Constraint;
import io.vlingo.xoom.auth.model.Constraint.Type;
import io.vlingo.xoom.auth.model.Permission;
import io.vlingo.xoom.auth.model.PermissionRepository;
import io.vlingo.xoom.auth.model.TenantId;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.resource.ResourceHandler;

public class PermissionResource extends ResourceHandler {
  private final PermissionRepository permissionRepository = RepositoryProvider.permissionRepository();

  public PermissionResource() { }

  public void register(final String tenantId, final PermissionData permissionData) {
    final Permission permission = Permission.with(TenantId.fromExisting(tenantId), permissionData.name, permissionData.description);
    permissionRepository.save(permission);
    completes().with(Response.of(Created, headers(of(Location, location(permission))), serialized(PermissionData.from(permission))));
  }

  public void enforce(final String tenantId, final String permissionName, final ConstraintData constraintData) {
    final TenantId existingTenantId = TenantId.fromExisting(tenantId);
    final Permission permission = permissionRepository.permissionOf(existingTenantId, permissionName);
    if (permission.doesNotExist()) {
      completes().with(Response.of(NotFound, location(existingTenantId, permissionName)));
    } else {
      permission.enforce(Constraint.of(Type.valueOf(constraintData.type), constraintData.name, constraintData.value, constraintData.description));
      permissionRepository.save(permission);
      completes().with(Response.of(Ok, serialized(PermissionData.from(permission))));
    }
  }

  public void enforceReplacement(final String tenantId, final String permissionName, final String constraintName, final ConstraintData constraintData) {
    final TenantId existingTenantId = TenantId.fromExisting(tenantId);
    final Permission permission = permissionRepository.permissionOf(existingTenantId, permissionName);
    if (permission.doesNotExist()) {
      completes().with(Response.of(NotFound, location(existingTenantId, permissionName)));
    } else {
      final Constraint previousConstraint = permission.constraintOf(constraintName);
      if (previousConstraint == null) {
        completes().with(Response.of(NotFound, "Constraint does not exist: " + constraintName));
      } else {
        final Constraint currentConstraint = Constraint.of(Type.valueOf(constraintData.type), constraintData.name, constraintData.value, constraintData.description);
        permission.enforce(previousConstraint, currentConstraint);
        permissionRepository.save(permission);
        completes().with(Response.of(Ok, serialized(PermissionData.from(permission))));
      }
    }
  }

  public void forget(final String tenantId, final String permissionName, final String constraintName) {
    final TenantId existingTenantId = TenantId.fromExisting(tenantId);
    final Permission permission = permissionRepository.permissionOf(existingTenantId, permissionName);
    if (permission.doesNotExist()) {
      completes().with(Response.of(NotFound, location(existingTenantId, permissionName)));
    } else {
      final Constraint constraint = permission.constraintOf(constraintName);
      if (constraint == null) {
        completes().with(Response.of(NotFound, "Constraint does not exist: " + constraintName));
      } else {
        permission.forget(constraint);
        permissionRepository.save(permission);
        completes().with(Response.of(Ok, serialized(PermissionData.from(permission))));
      }
    }
  }

  public void changeDescription(final String tenantId, final String permissionName, final String description) {
    final TenantId existingTenantId = TenantId.fromExisting(tenantId);
    final Permission permission = permissionRepository.permissionOf(existingTenantId, permissionName);
    if (permission.doesNotExist()) {
      completes().with(Response.of(NotFound, location(existingTenantId, permissionName)));
    } else {
      permission.changeDescription(description);
      permissionRepository.save(permission);
      completes().with(Response.of(Ok, serialized(PermissionData.from(permission))));
    }
  }

  public void queryPermission(String tenantId, String permissionName) {
    final Permission permission = permissionRepository.permissionOf(TenantId.fromExisting(tenantId), permissionName);
    if (permission.doesNotExist()) {
      completes().with(Response.of(NotFound, "Permission does not exist: " + permissionName));
    } else {
      completes().with(Response.of(Ok, serialized(PermissionData.from(permission))));
    }    
  }

  private String location(final Permission permission) {
    return location(permission.tenantId(), permission.name());
  }

  private String location(final TenantId tenantId, final String permissionName) {
    return "/tenants/" + tenantId.value + "/permissions/" + permissionName;
  }
}
