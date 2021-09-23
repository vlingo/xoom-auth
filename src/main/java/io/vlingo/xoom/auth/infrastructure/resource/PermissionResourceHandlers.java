package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.turbo.annotation.autodispatch.Handler.Three;
import io.vlingo.xoom.turbo.annotation.autodispatch.Handler.Two;
import io.vlingo.xoom.turbo.annotation.autodispatch.HandlerEntry;

import io.vlingo.xoom.auth.infrastructure.persistence.PermissionQueries;
import io.vlingo.xoom.auth.model.value.*;
import io.vlingo.xoom.auth.model.permission.Permission;
import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.model.permission.PermissionState;
import java.util.Collection;

public class PermissionResourceHandlers {

  public static final int PROVISION_PERMISSION = 0;
  public static final int ENFORCE = 1;
  public static final int ENFORCE_REPLACEMENT = 2;
  public static final int FORGET = 3;
  public static final int CHANGE_DESCRIPTION = 4;
  public static final int PERMISSIONS = 5;
  public static final int PERMISSION_OF = 6;
  public static final int ADAPT_STATE = 7;

  public static final HandlerEntry<Three<Completes<PermissionState>, Stage, PermissionData>> PROVISION_PERMISSION_HANDLER =
          HandlerEntry.of(PROVISION_PERMISSION, ($stage, data) -> {
              return Permission.provisionPermission($stage, data.description, data.name, data.tenantId);
          });

  public static final HandlerEntry<Three<Completes<PermissionState>, Permission, PermissionData>> ENFORCE_HANDLER =
          HandlerEntry.of(ENFORCE, (permission, data) -> {
              return permission.enforce(data.constraints.stream().map(ConstraintData::toConstraint).findFirst().get());
          });

  public static final HandlerEntry<Three<Completes<PermissionState>, Permission, PermissionData>> ENFORCE_REPLACEMENT_HANDLER =
          HandlerEntry.of(ENFORCE_REPLACEMENT, (permission, data) -> {
              return permission.enforceReplacement(data.constraints.stream().map(ConstraintData::toConstraint).findFirst().get());
          });

  public static final HandlerEntry<Three<Completes<PermissionState>, Permission, PermissionData>> FORGET_HANDLER =
          HandlerEntry.of(FORGET, (permission, data) -> {
              return permission.forget(data.constraints.stream().map(ConstraintData::toConstraint).findFirst().get());
          });

  public static final HandlerEntry<Three<Completes<PermissionState>, Permission, PermissionData>> CHANGE_DESCRIPTION_HANDLER =
          HandlerEntry.of(CHANGE_DESCRIPTION, (permission, data) -> {
              return permission.changeDescription(data.description, data.tenantId);
          });

  public static final HandlerEntry<Two<PermissionData, PermissionState>> ADAPT_STATE_HANDLER =
          HandlerEntry.of(ADAPT_STATE, PermissionData::from);

  public static final HandlerEntry<Two<Completes<Collection<PermissionData>>, PermissionQueries>> QUERY_ALL_HANDLER =
          HandlerEntry.of(PERMISSIONS, PermissionQueries::permissions);

  public static final HandlerEntry<Three<Completes<PermissionData>, PermissionQueries, String>> QUERY_BY_ID_HANDLER =
          HandlerEntry.of(PERMISSION_OF, ($queries, id) -> $queries.permissionOf(id));

}