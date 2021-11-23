package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.infrastructure.TenantData;
import io.vlingo.xoom.auth.model.tenant.*;
import io.vlingo.xoom.common.Completes;
import org.junit.jupiter.api.Test;

import static io.vlingo.xoom.auth.test.Assertions.assertCompletes;
import static org.junit.jupiter.api.Assertions.*;

public class TenantProjectionTest extends ProjectionTest {

  @Test
  public void itProjectsSubscribedTenant() {
    final TenantId tenantId = TenantId.unique();

    givenEvents(
            new TenantSubscribed(tenantId, "tenant-1", "Tenant 1", false)
    );

    assertCompletes(tenantOf(tenantId), tenant -> {
      assertEquals(tenantId.idString(), tenant.tenantId);
      assertEquals("tenant-1", tenant.name);
      assertEquals("Tenant 1", tenant.description);
      assertFalse(tenant.active);
    });
  }

  @Test
  public void itProjectsActivatedTenant() {
    final TenantId tenantId = TenantId.unique();

    givenEvents(
            new TenantSubscribed(tenantId, "tenant-1", "Tenant 1", false),
            new TenantActivated(tenantId)
    );

    assertCompletes(tenantOf(tenantId), tenant -> assertTrue(tenant.active));
  }

  @Test
  public void itProjectsDeactivatedTenant() {
    final TenantId tenantId = TenantId.unique();

    givenEvents(
            new TenantSubscribed(tenantId, "tenant-1", "Tenant 1", true),
            new TenantDeactivated(tenantId)
    );

    assertCompletes(tenantOf(tenantId), tenant -> assertFalse(tenant.active));
  }

  @Test
  public void itProjectsTenantNameChange() {
    final TenantId tenantId = TenantId.unique();

    givenEvents(
            new TenantSubscribed(tenantId, "tenant-1", "Tenant 1", true),
            new TenantNameChanged(tenantId, "tenant-1-updated")
    );

    assertCompletes(tenantOf(tenantId), tenant -> assertEquals("tenant-1-updated", tenant.name));
  }


  @Test
  public void itProjectsTenantDescriptionChange() {
    final TenantId tenantId = TenantId.unique();

    givenEvents(
            new TenantSubscribed(tenantId, "tenant-1", "Tenant 1", true),
            new TenantDescriptionChanged(tenantId, "Tenant 1 Updated")
    );

    assertCompletes(tenantOf(tenantId), tenant -> assertEquals("Tenant 1 Updated", tenant.description));
  }

  private Completes<TenantData> tenantOf(final TenantId tenantId) {
    return world.actorFor(TenantQueries.class, TenantQueriesActor.class, stateStore).tenantOf(tenantId);
  }
}
