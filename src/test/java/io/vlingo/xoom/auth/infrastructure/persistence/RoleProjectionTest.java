package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.permission.PermissionId;
import io.vlingo.xoom.auth.model.role.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.projection.Projection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleProjectionTest extends ProjectionTest {

  @Override
  protected Set<Class<?>> statefulTypes() {
    return Collections.singleton(RoleView.class);
  }

  @Override
  protected Projection projection() {
    return world.actorFor(Projection.class, RoleProjectionActor.class, stateStore);
  }

  @Test
  public void itProjectsTheProvisionedRole() {
    final RoleId roleId = RoleId.from(TenantId.unique(), "role-a");

    givenEvents(
            new RoleProvisioned(roleId, "role-a", "Role A")
    );

    assertCompletes(roleOf(roleId), role -> {
      assertEquals(roleId.idString(), role.id);
      assertEquals(roleId.tenantId.idString(), role.tenantId);
      assertEquals("role-a", role.name);
      assertEquals("Role A", role.description);
    });
  }

  @Test
  public void itProjectsTheRoleDescriptionUpdate() {
    final RoleId roleId = RoleId.from(TenantId.unique(), "role-a");

    givenEvents(
            new RoleProvisioned(roleId, "role-a", "Role A"),
            new RoleDescriptionChanged(roleId, "Role A updated")
    );

    assertCompletes(roleOf(roleId), role -> assertEquals("Role A updated", role.description));
  }

  @Test
  public void itProjectsPermissionAttachedToRole() {
    final RoleId roleId = RoleId.from(TenantId.unique(), "role-a");
    final PermissionId permissionIdA = PermissionId.from(roleId.tenantId, "permission-a");
    final PermissionId permissionIdB = PermissionId.from(roleId.tenantId, "permission-b");

    givenEvents(
            new RoleProvisioned(roleId, "role-a", "Role A"),
            new RolePermissionAttached(roleId, permissionIdA),
            new RolePermissionAttached(roleId, permissionIdB)
    );

    assertCompletes(roleOf(roleId), role -> assertEquals(
            setOf(Relation.permissionAttachedToRole(permissionIdA, roleId), Relation.permissionAttachedToRole(permissionIdB, roleId)),
            role.permissions
    ));
  }

  @Test
  public void itProjectsPermissionDetachedFromRole() {
    final RoleId roleId = RoleId.from(TenantId.unique(), "role-a");
    final PermissionId permissionIdA = PermissionId.from(roleId.tenantId, "permission-a");
    final PermissionId permissionIdB = PermissionId.from(roleId.tenantId, "permission-b");

    givenEvents(
            new RoleProvisioned(roleId, "role-a", "Role A"),
            new RolePermissionAttached(roleId, permissionIdA),
            new RolePermissionAttached(roleId, permissionIdB),
            new RolePermissionDetached(roleId, permissionIdA)
    );

    assertCompletes(roleOf(roleId), role -> assertEquals(
            setOf(Relation.permissionAttachedToRole(permissionIdB, roleId)),
            role.permissions
    ));
  }

  @Test
  public void itProjectsGroupAssignedToRole() {
    final RoleId roleId = RoleId.from(TenantId.unique(), "role-a");
    final GroupId groupIdA = GroupId.from(roleId.tenantId, "group-a");
    final GroupId groupIdB = GroupId.from(roleId.tenantId, "group-b");

    givenEvents(
            new RoleProvisioned(roleId, "role-a", "Role A"),
            new GroupAssignedToRole(roleId, groupIdA),
            new GroupAssignedToRole(roleId, groupIdB)
    );

    assertCompletes(roleOf(roleId), role -> assertEquals(
            setOf(Relation.groupAssignedToRole(groupIdA, roleId), Relation.groupAssignedToRole(groupIdB, roleId)),
            role.groups
    ));
  }


  @Test
  public void itProjectsGroupUnassignedFromRole() {
    final RoleId roleId = RoleId.from(TenantId.unique(), "role-a");
    final GroupId groupIdA = GroupId.from(roleId.tenantId, "group-a");
    final GroupId groupIdB = GroupId.from(roleId.tenantId, "group-b");

    givenEvents(
            new RoleProvisioned(roleId, "role-a", "Role A"),
            new GroupAssignedToRole(roleId, groupIdA),
            new GroupAssignedToRole(roleId, groupIdB),
            new GroupUnassignedFromRole(roleId, groupIdA)
    );

    assertCompletes(roleOf(roleId), role -> assertEquals(
            setOf(Relation.groupAssignedToRole(groupIdB, roleId)),
            role.groups
    ));
  }

  @Test
  public void itProjectsUserAssignedToRole() {
    final RoleId roleId = RoleId.from(TenantId.unique(), "role-a");
    final UserId userIdAlice = UserId.from(roleId.tenantId, "alice");
    final UserId userIdBob = UserId.from(roleId.tenantId, "bob");

    givenEvents(
            new RoleProvisioned(roleId, "role-a", "Role A"),
            new UserAssignedToRole(roleId, userIdAlice),
            new UserAssignedToRole(roleId, userIdBob)
    );

    assertCompletes(roleOf(roleId), role -> assertEquals(
            setOf(Relation.userAssignedToRole(userIdAlice, roleId), Relation.userAssignedToRole(userIdBob, roleId)),
            role.users
    ));
  }


  @Test
  public void itProjectsUserUnassignedFromRole() {
    final RoleId roleId = RoleId.from(TenantId.unique(), "role-a");
    final UserId userIdAlice = UserId.from(roleId.tenantId, "alice");
    final UserId userIdBob = UserId.from(roleId.tenantId, "bob");

    givenEvents(
            new RoleProvisioned(roleId, "role-a", "Role A"),
            new UserAssignedToRole(roleId, userIdAlice),
            new UserAssignedToRole(roleId, userIdBob),
            new UserUnassignedFromRole(roleId, userIdAlice)
    );

    assertCompletes(roleOf(roleId), role -> assertEquals(
            setOf(Relation.userAssignedToRole(userIdBob, roleId)),
            role.users
    ));
  }

  private Completes<RoleView> roleOf(final RoleId roleId) {
    return world.actorFor(RoleQueries.class, RoleQueriesActor.class, stateStore).roleOf(roleId);
  }

  private <T> Set<T> setOf(T... elements) {
    return new HashSet<>(Arrays.asList(elements));
  }
}
