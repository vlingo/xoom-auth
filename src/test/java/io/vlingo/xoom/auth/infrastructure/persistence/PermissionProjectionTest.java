package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.persistence.PermissionView.ConstraintView;
import io.vlingo.xoom.auth.model.permission.*;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.value.Constraint;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.projection.Projection;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static io.vlingo.xoom.auth.test.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PermissionProjectionTest extends ProjectionTest {

  @Override
  protected Set<Class<?>> statefulTypes() {
    return Collections.singleton(PermissionView.class);
  }

  @Override
  protected Projection projection() {
    return world.actorFor(Projection.class, PermissionProjectionActor.class, stateStore);
  }

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

  private Completes<PermissionView> permissionOf(PermissionId permissionId) {
    return world.actorFor(PermissionQueries.class, PermissionQueriesActor.class, stateStore).permissionOf(permissionId);
  }
}
