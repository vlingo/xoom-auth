package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.persistence.PermissionView.ConstraintView;
import io.vlingo.xoom.auth.model.permission.*;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.role.RolePermissionAttached;
import io.vlingo.xoom.auth.model.role.RolePermissionDetached;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.value.Constraint;
import io.vlingo.xoom.common.Completes;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static io.vlingo.xoom.auth.test.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class PermissionProjectionTest extends ProjectionTest {

  @Test
  public void itProjectsProvisionedPermission() {
    final PermissionId permissionId = PermissionId.from(TenantId.unique(), "permission-a");

    givenEvents(
            new PermissionProvisioned(permissionId, "permission-a", "Permission A")
    );

    assertCompletes(permissionOf(permissionId), permission -> {
      assertEquals(permissionId.idString(), permission.id);
      assertEquals(permissionId.tenantId.idString(), permission.tenantId);
      assertEquals("permission-a", permission.name);
      assertEquals("Permission A", permission.description);
    });
  }

  @Test
  public void itProjectsEnforcedConstraint() {
    final PermissionId permissionId = PermissionId.from(TenantId.unique(), "permission-a");
    final Constraint constraintA = Constraint.from(Constraint.Type.String, "constraint-a", "2", "Constraint A");
    final Constraint constraintB = Constraint.from(Constraint.Type.String, "constraint-b", "B", "Constraint B");

    givenEvents(
            new PermissionProvisioned(permissionId, "permission-a", "Permission A"),
            new PermissionConstraintEnforced(permissionId, constraintA),
            new PermissionConstraintEnforced(permissionId, constraintB)
    );

    assertCompletes(permissionOf(permissionId), permission -> {
      assertContains(ConstraintView.from(constraintA), permission.constraints);
      assertContains(ConstraintView.from(constraintB), permission.constraints);
    });
  }

  @Test
  public void itProjectsEnforcedConstraintReplacement() {
    final PermissionId permissionId = PermissionId.from(TenantId.unique(), "permission-a");
    final Constraint constraintA = Constraint.from(Constraint.Type.String, "constraint-a", "2", "Constraint A");
    final Constraint constraintB = Constraint.from(Constraint.Type.String, "constraint-b", "B", "Constraint B");

    givenEvents(
            new PermissionProvisioned(permissionId, "permission-a", "Permission A"),
            new PermissionConstraintEnforced(permissionId, constraintA),
            new PermissionConstraintReplacementEnforced(permissionId, "constraint-a", constraintB)
    );

    assertCompletes(permissionOf(permissionId), permission -> {
      assertNotContains(ConstraintView.from(constraintA), permission.constraints);
      assertContains(ConstraintView.from(constraintB), permission.constraints);
    });
  }

  @Test
  public void itProjectsForgottenConstraint() {
    final PermissionId permissionId = PermissionId.from(TenantId.unique(), "permission-a");
    final Constraint constraintA = Constraint.from(Constraint.Type.String, "constraint-a", "2", "Constraint A");
    final Constraint constraintB = Constraint.from(Constraint.Type.String, "constraint-b", "B", "Constraint B");

    givenEvents(
            new PermissionProvisioned(permissionId, "permission-a", "Permission A"),
            new PermissionConstraintEnforced(permissionId, constraintA),
            new PermissionConstraintEnforced(permissionId, constraintB),
            new PermissionConstraintForgotten(permissionId, "constraint-a")
    );

    assertCompletes(permissionOf(permissionId), permission -> {
      assertNotContains(ConstraintView.from(constraintA), permission.constraints);
      assertContains(ConstraintView.from(constraintB), permission.constraints);
    });
  }

  @Test
  public void itProjectsConstraintDescriptionChange() {
    final PermissionId permissionId = PermissionId.from(TenantId.unique(), "permission-a");

    givenEvents(
            new PermissionProvisioned(permissionId, "permission-a", "Permission A"),
            new PermissionDescriptionChanged(permissionId, "Permission A updated")
    );

    assertCompletes(permissionOf(permissionId), permission -> assertEquals("Permission A updated", permission.description));
  }

  @Test
  public void itProjectsRolePermissionAttached() {
    final PermissionId permissionId = PermissionId.from(TenantId.unique(), "permission-a");
    final RoleId roleId = RoleId.from(permissionId.tenantId, "role-a");

    givenEvents(
            new PermissionProvisioned(permissionId, "permission-a", "Permission A"),
            new RolePermissionAttached(roleId, permissionId)
    );

    assertCompletes(permissionOf(permissionId), permission -> {
      assertEquals(setOf(Relation.roleWithPermission(roleId, permissionId)), permission.roles);
      assertTrue(permission.isAttachedTo(roleId));
      assertFalse(permission.isAttachedTo(RoleId.from(permissionId.tenantId, "role-b")));
    });
  }

  @Test
  public void itProjectsRolePermissionDetached() {
    final PermissionId permissionId = PermissionId.from(TenantId.unique(), "permission-a");
    final RoleId roleIdA = RoleId.from(permissionId.tenantId, "role-a");
    final RoleId roleIdB = RoleId.from(permissionId.tenantId, "role-b");

    givenEvents(
            new PermissionProvisioned(permissionId, "permission-a", "Permission A"),
            new RolePermissionAttached(roleIdA, permissionId),
            new RolePermissionAttached(roleIdB, permissionId),
            new RolePermissionDetached(roleIdA, permissionId)
    );

    assertCompletes(permissionOf(permissionId), permission -> {
      assertEquals(setOf(Relation.roleWithPermission(roleIdB, permissionId)), permission.roles);
      assertFalse(permission.isAttachedTo(roleIdA));
      assertTrue(permission.isAttachedTo(roleIdB));
    });
  }

  private Completes<PermissionView> permissionOf(PermissionId permissionId) {
    return world.actorFor(PermissionQueries.class, PermissionQueriesActor.class, stateStore).permissionOf(permissionId);
  }

  private <T> Set<T> setOf(T... elements) {
    return new HashSet<>(Arrays.asList(elements));
  }
}
