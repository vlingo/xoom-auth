package io.vlingo.xoom.auth.model.tenant;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.actors.Stage;

public interface Tenant {

  Completes<TenantState> subscribeFor();

  static Completes<TenantState> subscribeFor(final Stage stage) {
    final io.vlingo.xoom.actors.Address _address = stage.addressFactory().uniquePrefixedWith("g-");
    final Tenant _tenant = stage.actorFor(Tenant.class, Definition.has(TenantEntity.class, Definition.parameters(_address.idString())), _address);
    return _tenant.subscribeFor();
  }

  Completes<TenantState> activate(final String id);

  Completes<TenantState> deactivate(final String id);

  Completes<TenantState> changeName(final String id, final String name);

  Completes<TenantState> changeDescription(final String id, final String description);

}