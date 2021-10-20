package io.vlingo.xoom.auth.model.tenant;


public final class TenantState {

  public final TenantId tenantId;
  public final String name;
  public final String description;
  public final boolean active;

  public static TenantState identifiedBy(final TenantId tenantId) {
    return new TenantState(tenantId, null, null, false);
  }

  public TenantState(final TenantId tenantId, final String name, final String description, final boolean active) {
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.active = active;
  }

  public TenantState subscribeFor(final String name, final String description, final boolean active) {
    return new TenantState(this.tenantId, name, description, active);
  }

  public TenantState activate() {
    return new TenantState(this.tenantId, this.name, this.description, true);
  }

  public TenantState deactivate() {
    return new TenantState(this.tenantId, this.name, this.description, false);
  }

  public TenantState changeName(final String name) {
    return new TenantState(this.tenantId, name, this.description, this.active);
  }

  public TenantState changeDescription(final String description) {
    return new TenantState(this.tenantId, this.name, description, this.active);
  }
}
