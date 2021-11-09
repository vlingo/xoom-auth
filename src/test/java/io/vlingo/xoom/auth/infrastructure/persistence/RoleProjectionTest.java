package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.role.GroupAssignedToRole;
import io.vlingo.xoom.auth.model.role.RoleDescriptionChanged;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.role.RoleProvisioned;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.projection.Projection;
import org.junit.jupiter.api.Test;

import java.util.Collections;
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
  public void itProjectsGroupAssignedToRole() {
    final RoleId roleId = RoleId.from(TenantId.unique(), "role-a");
    final GroupId groupId = GroupId.from(roleId.tenantId, "group-a");

    givenEvents(
            new RoleProvisioned(roleId, "role-a", "Role A"),
            new GroupAssignedToRole(roleId, groupId)
    );

    // @TODO finish the test
  }

  // @TODO finish the projection

  private Completes<RoleView> roleOf(final RoleId roleId) {
    return world.actorFor(RoleQueries.class, RoleQueriesActor.class, stateStore).roleOf(roleId);
  }
}
