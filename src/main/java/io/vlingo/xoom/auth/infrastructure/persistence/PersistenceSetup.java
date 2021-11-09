package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType;
import io.vlingo.xoom.turbo.annotation.persistence.Projections;
import io.vlingo.xoom.turbo.annotation.persistence.Projection;
import io.vlingo.xoom.turbo.annotation.persistence.ProjectionType;
import io.vlingo.xoom.turbo.annotation.persistence.Adapters;
import io.vlingo.xoom.turbo.annotation.persistence.EnableQueries;
import io.vlingo.xoom.turbo.annotation.persistence.QueriesEntry;
import io.vlingo.xoom.turbo.annotation.persistence.DataObjects;
import io.vlingo.xoom.auth.model.role.GroupUnassignedFromRole;
import io.vlingo.xoom.auth.model.tenant.TenantDeactivated;
import io.vlingo.xoom.auth.model.user.UserRegistered;
import io.vlingo.xoom.auth.model.permission.PermissionProvisioned;
import io.vlingo.xoom.auth.model.tenant.TenantActivated;
import io.vlingo.xoom.auth.model.role.RolePermissionDetached;
import io.vlingo.xoom.auth.model.user.UserCredentialReplaced;
import io.vlingo.xoom.auth.model.role.GroupAssignedToRole;
import io.vlingo.xoom.auth.model.permission.PermissionDescriptionChanged;
import io.vlingo.xoom.auth.model.permission.PermissionConstraintReplacementEnforced;
import io.vlingo.xoom.auth.model.user.UserCredentialRemoved;
import io.vlingo.xoom.auth.model.permission.PermissionConstraintForgotten;
import io.vlingo.xoom.auth.model.role.UserAssignedToRole;
import io.vlingo.xoom.auth.model.group.GroupDescriptionChanged;
import io.vlingo.xoom.auth.infrastructure.UserRegistrationData;
import io.vlingo.xoom.auth.model.tenant.TenantSubscribed;
import io.vlingo.xoom.auth.model.group.UserAssignedToGroup;
import io.vlingo.xoom.auth.model.tenant.TenantNameChanged;
import io.vlingo.xoom.auth.model.group.GroupProvisioned;
import io.vlingo.xoom.auth.model.role.UserUnassignedFromRole;
import io.vlingo.xoom.auth.model.user.UserActivated;
import io.vlingo.xoom.auth.model.role.RoleProvisioned;
import io.vlingo.xoom.auth.model.group.GroupAssignedToGroup;
import io.vlingo.xoom.auth.model.role.RoleDescriptionChanged;
import io.vlingo.xoom.auth.model.user.UserProfileReplaced;
import io.vlingo.xoom.auth.model.user.UserCredentialAdded;
import io.vlingo.xoom.auth.model.user.UserDeactivated;
import io.vlingo.xoom.auth.model.role.RolePermissionAttached;
import io.vlingo.xoom.auth.model.permission.PermissionConstraintEnforced;
import io.vlingo.xoom.auth.model.group.GroupUnassignedFromGroup;
import io.vlingo.xoom.auth.model.tenant.TenantDescriptionChanged;
import io.vlingo.xoom.auth.model.group.UserUnassignedFromGroup;

@SuppressWarnings("unused")
@Persistence(basePackage = "io.vlingo.xoom.auth", storageType = StorageType.JOURNAL, cqrs = true)
@Projections(value = {
  @Projection(actor = RoleProjectionActor.class, becauseOf = {UserAssignedToRole.class, RolePermissionAttached.class, GroupUnassignedFromRole.class, RoleDescriptionChanged.class, RoleProvisioned.class, UserUnassignedFromRole.class, RolePermissionDetached.class, GroupAssignedToRole.class}),
  @Projection(actor = TenantProjectionActor.class, becauseOf = {TenantSubscribed.class, TenantDeactivated.class, TenantNameChanged.class, TenantDescriptionChanged.class, TenantActivated.class}),
  @Projection(actor = GroupProjectionActor.class, becauseOf = {GroupDescriptionChanged.class, UserAssignedToGroup.class, GroupAssignedToGroup.class, GroupUnassignedFromGroup.class, UserUnassignedFromGroup.class, GroupProvisioned.class}),
  @Projection(actor = UserProjectionActor.class, becauseOf = {UserCredentialAdded.class, UserProfileReplaced.class, UserCredentialRemoved.class, UserDeactivated.class, UserRegistered.class, UserActivated.class, UserCredentialReplaced.class}),
  @Projection(actor = PermissionProjectionActor.class, becauseOf = {PermissionConstraintEnforced.class, PermissionDescriptionChanged.class, PermissionProvisioned.class, PermissionConstraintReplacementEnforced.class, PermissionConstraintForgotten.class})
}, type = ProjectionType.EVENT_BASED)
@Adapters({
  RolePermissionAttached.class,
  UserUnassignedFromRole.class,
  UserRegistered.class,
  TenantDeactivated.class,
  PermissionProvisioned.class,
  TenantDescriptionChanged.class,
  UserAssignedToRole.class,
  UserAssignedToGroup.class,
  UserCredentialRemoved.class,
  PermissionConstraintEnforced.class,
  GroupAssignedToGroup.class,
  TenantNameChanged.class,
  UserCredentialReplaced.class,
  GroupAssignedToRole.class,
  GroupUnassignedFromRole.class,
  GroupDescriptionChanged.class,
  GroupUnassignedFromGroup.class,
  RoleProvisioned.class,
  UserUnassignedFromGroup.class,
  GroupProvisioned.class,
  TenantActivated.class,
  PermissionConstraintForgotten.class,
  UserCredentialAdded.class,
  UserProfileReplaced.class,
  PermissionDescriptionChanged.class,
  UserDeactivated.class,
  RoleDescriptionChanged.class,
  TenantSubscribed.class,
  UserActivated.class,
  RolePermissionDetached.class,
  PermissionConstraintReplacementEnforced.class
})
@EnableQueries({
  @QueriesEntry(protocol = RoleQueries.class, actor = RoleQueriesActor.class),
  @QueriesEntry(protocol = GroupQueries.class, actor = GroupQueriesActor.class),
  @QueriesEntry(protocol = TenantQueries.class, actor = TenantQueriesActor.class),
  @QueriesEntry(protocol = UserQueries.class, actor = UserQueriesActor.class),
  @QueriesEntry(protocol = PermissionQueries.class, actor = PermissionQueriesActor.class),
})
@DataObjects({CredentialData.class, ConstraintData.class, UserRegistrationData.class, TenantData.class, PersonNameData.class, ProfileData.class, PermissionView.class, RoleView.class, GroupView.class})
public class PersistenceSetup {


}