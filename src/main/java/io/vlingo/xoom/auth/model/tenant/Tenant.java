package io.vlingo.xoom.auth.model.tenant;

import io.vlingo.xoom.common.Completes;

public interface Tenant {

  Completes<TenantState> subscribeFor(final String name, final String description, final boolean active);

  Completes<TenantState> activate();

  Completes<TenantState> deactivate();

  Completes<TenantState> changeName(final String name);

  Completes<TenantState> changeDescription(final String description);

}