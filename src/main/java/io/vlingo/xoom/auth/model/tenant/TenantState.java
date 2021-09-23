package io.vlingo.xoom.auth.model.tenant;


public final class TenantState {

  public final String id;
  public final String name;
  public final String description;
  public final boolean active;

  public static TenantState identifiedBy(final String id) {
    return new TenantState(id, null, null, false);
  }

  public TenantState (final String id, final String name, final String description, final boolean active) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.active = active;
  }

  public TenantState subscribeFor(final String name, final String description, final boolean active) {
    return new TenantState(this.id, name, description, active);
  }

  public TenantState activate() {
    //TODO: Implement command logic.
    return new TenantState(this.id, this.name, this.description, this.active);
  }

  public TenantState deactivate() {
    //TODO: Implement command logic.
    return new TenantState(this.id, this.name, this.description, this.active);
  }

  public TenantState changeName(final String name) {
    //TODO: Implement command logic.
    return new TenantState(this.id, name, this.description, this.active);
  }

  public TenantState changeDescription(final String description) {
    //TODO: Implement command logic.
    return new TenantState(this.id, this.name, description, this.active);
  }

}
