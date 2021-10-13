package io.vlingo.xoom.auth.model.user;

import io.vlingo.xoom.auth.model.tenant.TenantId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserIdTest {
  @Test
  public void itIsComposedOfTenantIdAndUsername() {
    final UserId userId = UserId.from(TenantId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be"), "alice");

    assertEquals("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be", userId.tenantId.id);
    assertEquals("alice", userId.username);
    assertEquals("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be:alice", userId.idString());
  }

  @Test
  public void twoUserIdsAreTheSameIfTenantIdAndUsernameAreTheSame() {
    final UserId userId = UserId.from(TenantId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be"), "alice");

    assertEquals(userId, UserId.from(TenantId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be"), "alice"));
    assertNotEquals(userId, UserId.from(TenantId.from("97d57df1-1b38-4583-bd3c-6b4731e7a605"), "alice"));
    assertNotEquals(userId, UserId.from(TenantId.from("c60317e9-cf4f-408a-9fd1-8e0f4e69d2be"), "bob"));
  }
}
