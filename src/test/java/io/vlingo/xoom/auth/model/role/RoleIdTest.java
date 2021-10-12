package io.vlingo.xoom.auth.model.role;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RoleIdTest {
  @Test
  public void itIsComposedOfTenantIdAndRoleId() {
    final RoleId roleId = RoleId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6");

    assertEquals("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", roleId.tenantId);
    assertEquals("2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6", roleId.roleName);
    assertEquals("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be:2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6", roleId.idString());
  }

  @Test
  public void twoRoleIdsAreTheSameIfTenantIdAndRoleIdAreTheSame() {
    final RoleId roleId = RoleId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6");

    assertEquals(roleId, RoleId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6"));
    assertNotEquals(roleId, RoleId.from("97d57df1-1b38-4583-bd3c-6b4731e7a605", "2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6"));
    assertNotEquals(roleId, RoleId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "0428d634-f81b-4952-8886-fd15a34292f0"));
  }
}
