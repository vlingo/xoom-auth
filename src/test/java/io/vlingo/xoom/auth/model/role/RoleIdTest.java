package io.vlingo.xoom.auth.model.role;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RoleIdTest {
  @Test
  public void itIsComposedOfTenantIdAndRoleId() {
    final RoleId roleId = RoleId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "role-a");

    assertEquals("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", roleId.tenantId);
    assertEquals("role-a", roleId.roleName);
    assertEquals("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be:role-a", roleId.idString());
  }

  @Test
  public void twoRoleIdsAreTheSameIfTenantIdAndRoleIdAreTheSame() {
    final RoleId roleId = RoleId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "role-a");

    assertEquals(roleId, RoleId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "role-a"));
    assertNotEquals(roleId, RoleId.from("97d57df1-1b38-4583-bd3c-6b4731e7a605", "role-a"));
    assertNotEquals(roleId, RoleId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "role-b"));
  }
}
