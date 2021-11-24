package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.*;
import io.vlingo.xoom.auth.model.group.*;
import io.vlingo.xoom.auth.model.permission.*;
import io.vlingo.xoom.auth.model.role.*;
import io.vlingo.xoom.auth.model.tenant.*;
import io.vlingo.xoom.auth.model.user.*;
import io.vlingo.xoom.turbo.annotation.persistence.*;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType;

@SuppressWarnings("unused")
@Persistence(basePackage = "io.vlingo.xoom.auth", storageType = StorageType.JOURNAL, cqrs = true)
@Projections(value = {
  @Projection(actor = RoleProjectionActor.class, becauseOf = {UserAssignedToRole.class, RolePermissionAttached.class, GroupUnassignedFromRole.class, RoleDescriptionChanged.class, RoleProvisioned.class, UserUnassignedFromRole.class, RolePermissionDetached.class, GroupAssignedToRole.class}),
  @Projection(actor = TenantProjectionActor.class, becauseOf = {TenantSubscribed.class, TenantDeactivated.class, TenantNameChanged.class, TenantDescriptionChanged.class, TenantActivated.class}),
  @Projection(actor = GroupProjectionActor.class, becauseOf = {GroupDescriptionChanged.class, UserAssignedToGroup.class, GroupAssignedToGroup.class, GroupUnassignedFromGroup.class, UserUnassignedFromGroup.class, GroupProvisioned.class, GroupAssignedToRole.class, GroupUnassignedFromRole.class}),
  @Projection(actor = UserProjectionActor.class, becauseOf = {UserCredentialAdded.class, UserProfileReplaced.class, UserCredentialRemoved.class, UserDeactivated.class, UserRegistered.class, UserActivated.class, UserCredentialReplaced.class, UserAssignedToRole.class, UserUnassignedFromRole.class}),
  @Projection(actor = PermissionProjectionActor.class, becauseOf = {PermissionConstraintEnforced.class, PermissionDescriptionChanged.class, PermissionProvisioned.class, PermissionConstraintReplacementEnforced.class, PermissionConstraintForgotten.class, RolePermissionAttached.class, RolePermissionDetached.class})
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
@DataObjects({CredentialData.class, ConstraintData.class, UserView.class, TenantData.class, PersonNameData.class, ProfileData.class, PermissionView.class, RoleView.class, GroupView.class})
public class PersistenceSetup {


}