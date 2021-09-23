package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.turbo.annotation.persistence.Persistence;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType;
import io.vlingo.xoom.turbo.annotation.persistence.Projections;
import io.vlingo.xoom.turbo.annotation.persistence.Projection;
import io.vlingo.xoom.turbo.annotation.persistence.ProjectionType;
import io.vlingo.xoom.turbo.annotation.persistence.Adapters;
import io.vlingo.xoom.turbo.annotation.persistence.EnableQueries;
import io.vlingo.xoom.turbo.annotation.persistence.QueriesEntry;
import io.vlingo.xoom.turbo.annotation.persistence.DataObjects;
import io.vlingo.xoom.auth.infrastructure.ConstraintData;
import io.vlingo.xoom.auth.model.role.GroupUnassignedFromRole;
import io.vlingo.xoom.auth.model.tenant.TenantState;
import io.vlingo.xoom.auth.model.tenant.TenantDeactivated;
import io.vlingo.xoom.auth.infrastructure.GroupData;
import io.vlingo.xoom.auth.model.user.UserRegistered;
import io.vlingo.xoom.auth.model.permission.PermissionProvisioned;
import io.vlingo.xoom.auth.model.tenant.TenantActivated;
import io.vlingo.xoom.auth.model.role.RolePermissionDetached;
import io.vlingo.xoom.auth.model.permission.PermissionState;
import io.vlingo.xoom.auth.model.group.GroupState;
import io.vlingo.xoom.auth.model.user.UserCredentialReplaced;
import io.vlingo.xoom.auth.model.role.GroupAssignedToRole;
import io.vlingo.xoom.auth.infrastructure.PermissionData;
import io.vlingo.xoom.auth.model.permission.PermissionDescriptionChanged;
import io.vlingo.xoom.auth.model.role.RoleState;
import io.vlingo.xoom.auth.model.permission.PermissionConstraintReplacementEnforced;
import io.vlingo.xoom.auth.infrastructure.ProfileData;
import io.vlingo.xoom.auth.model.user.UserCredentialRemoved;
import io.vlingo.xoom.auth.model.permission.PermissionConstraintForgotten;
import io.vlingo.xoom.auth.infrastructure.TenantData;
import io.vlingo.xoom.auth.model.role.UserAssignedToRole;
import io.vlingo.xoom.auth.model.group.GroupDescriptionChanged;
import io.vlingo.xoom.auth.infrastructure.UserData;
import io.vlingo.xoom.auth.infrastructure.PersonNameData;
import io.vlingo.xoom.auth.infrastructure.CredentialData;
import io.vlingo.xoom.auth.model.group.UserAssignedToGroup;
import io.vlingo.xoom.auth.model.tenant.TenantSubscribed;
import io.vlingo.xoom.auth.model.tenant.TenantNameChanged;
import io.vlingo.xoom.auth.model.user.UserState;
import io.vlingo.xoom.auth.model.group.GroupProvisioned;
import io.vlingo.xoom.auth.model.role.UserUnassignedFromRole;
import io.vlingo.xoom.auth.model.user.UserActivated;
import io.vlingo.xoom.auth.model.role.RoleProvisioned;
import io.vlingo.xoom.auth.model.group.GroupAssignedToGroup;
import io.vlingo.xoom.auth.model.role.RoleDescriptionChanged;
import io.vlingo.xoom.auth.model.user.UserProfileReplaced;
import io.vlingo.xoom.auth.model.user.UserCredentialAdded;
import io.vlingo.xoom.auth.infrastructure.RoleData;
import io.vlingo.xoom.auth.model.user.UserDeactivated;
import io.vlingo.xoom.auth.model.role.RolePermissionAttached;
import io.vlingo.xoom.auth.model.permission.PermissionConstraintEnforced;
import io.vlingo.xoom.auth.model.group.GroupUnassignedFromGroup;
import io.vlingo.xoom.auth.model.group.UserUnassignedFromGroup;
import io.vlingo.xoom.auth.model.tenant.TenantDescriptionChanged;

@SuppressWarnings("unused")
@Persistence(basePackage = "io.vlingo.xoom.auth", storageType = StorageType.JOURNAL, cqrs = true)
@Projections(value = {
  @Projection(actor = GroupProjectionActor.class, becauseOf = {GroupDescriptionChanged.class, UserAssignedToGroup.class, GroupAssignedToGroup.class, GroupUnassignedFromGroup.class, UserUnassignedFromGroup.class, GroupProvisioned.class}),
  @Projection(actor = RoleProjectionActor.class, becauseOf = {UserAssignedToRole.class, RolePermissionAttached.class, GroupUnassignedFromRole.class, RoleDescriptionChanged.class, RoleProvisioned.class, UserUnassignedFromRole.class, RolePermissionDetached.class, GroupAssignedToRole.class}),
  @Projection(actor = UserProjectionActor.class, becauseOf = {UserCredentialAdded.class, UserProfileReplaced.class, UserCredentialRemoved.class, UserDeactivated.class, UserRegistered.class, UserActivated.class, UserCredentialReplaced.class}),
  @Projection(actor = TenantProjectionActor.class, becauseOf = {TenantSubscribed.class, TenantDeactivated.class, TenantNameChanged.class, TenantDescriptionChanged.class, TenantActivated.class}),
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
  UserCredentialReplaced.class,
  TenantNameChanged.class,
  GroupAssignedToRole.class,
  GroupDescriptionChanged.class,
  GroupUnassignedFromRole.class,
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
  @QueriesEntry(protocol = UserQueries.class, actor = UserQueriesActor.class),
  @QueriesEntry(protocol = TenantQueries.class, actor = TenantQueriesActor.class),
  @QueriesEntry(protocol = PermissionQueries.class, actor = PermissionQueriesActor.class),
})
@DataObjects({CredentialData.class, ConstraintData.class, UserData.class, TenantData.class, PersonNameData.class, ProfileData.class, PermissionData.class, GroupData.class, RoleData.class})
public class PersistenceSetup {


}