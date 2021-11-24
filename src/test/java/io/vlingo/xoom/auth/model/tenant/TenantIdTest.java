package io.vlingo.xoom.auth.model.tenant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TenantIdTest {
  @Test
  public void itWrapsTenantId() {
    final TenantId tenantId = TenantId.from("c9ca8a96-eccd-416c-aba5-8c36cbaec717");

    assertEquals("c9ca8a96-eccd-416c-aba5-8c36cbaec717", tenantId.id);
    assertEquals("c9ca8a96-eccd-416c-aba5-8c36cbaec717", tenantId.idString());
  }

  @Test
  public void twoTenantIdsAreTheSameIfItsStringIdsAreEqual() {
    final TenantId tenantId = TenantId.from("c9ca8a96-eccd-416c-aba5-8c36cbaec717");

    assertEquals(tenantId, TenantId.from("c9ca8a96-eccd-416c-aba5-8c36cbaec717"));
    assertNotEquals(tenantId, TenantId.from("97d57df1-1b38-4583-bd3c-6b4731e7a605"));
  }

  @Test
  public void itGeneratesNewId() {
    final TenantId tenantId = TenantId.unique();

    assertNotNull(tenantId.id);
  }
}
