package io.vlingo.xoom.auth.model.permission;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PermissionIdTest {
  @Test
  public void itIsComposedOfTenantIdAndPermissionId() {
    final PermissionId permissionId = PermissionId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "permission-a");

    assertEquals("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", permissionId.tenantId);
    assertEquals("permission-a", permissionId.permissionName);
    assertEquals("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be:permission-a", permissionId.idString());
  }

  @Test
  public void twoPermissionIdsAreTheSameIfTenantIdAndPermissionIdAreTheSame() {
    final PermissionId permissionId = PermissionId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "permission-a");

    assertEquals(permissionId, PermissionId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "permission-a"));
    assertNotEquals(permissionId, PermissionId.from("97d57df1-1b38-4583-bd3c-6b4731e7a605", "permission-a"));
    assertNotEquals(permissionId, PermissionId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", "permission-b"));
  }
}
