package io.vlingo.xoom.auth.model.permission;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PermissionIdTest {
  @Test
  public void itIsComposedOfTenantIdAndPermissionId() {
    final PermissionId permissionId = PermissionId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6");

    assertEquals("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", permissionId.tenantId);
    assertEquals("2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6", permissionId.permissionName);
    assertEquals("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be:2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6", permissionId.idString());
  }

  @Test
  public void twoPermissionIdsAreTheSameIfTenantIdAndPermissionIdAreTheSame() {
    final PermissionId permissionId = PermissionId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6");

    assertEquals(permissionId, PermissionId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6"));
    assertNotEquals(permissionId, PermissionId.from("97d57df1-1b38-4583-bd3c-6b4731e7a605", "2cfa9bfb-6cdc-47b6-b155-16877dc2cdf6"));
    assertNotEquals(permissionId, PermissionId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "0428d634-f81b-4952-8886-fd15a34292f0"));
  }
}
