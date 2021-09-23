package io.vlingo.xoom.auth.model.tenant;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.actors.Stage;

public interface Tenant {

  Completes<TenantState> subscribeFor(final String name, final String description, final boolean active);

  static Completes<TenantState> subscribeFor(final Stage stage, final String name, final String description, final boolean active) {
    final io.vlingo.xoom.actors.Address _address = stage.addressFactory().uniquePrefixedWith("g-");
    final Tenant _tenant = stage.actorFor(Tenant.class, Definition.has(TenantEntity.class, Definition.parameters(_address.idString())), _address);
    return _tenant.subscribeFor(name, description, active);
  }

  Completes<TenantState> activate();

  Completes<TenantState> deactivate();

  Completes<TenantState> changeName(final String name);

  Completes<TenantState> changeDescription(final String description);

}