package io.vlingo.xoom.auth.infrastructure.exchange;

import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.lattice.exchange.Exchange;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.dispatch.ConfirmDispatchedResultInterest;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import io.vlingo.xoom.auth.model.role.GroupUnassignedFromRole;
import io.vlingo.xoom.auth.model.tenant.TenantDeactivated;
import io.vlingo.xoom.auth.model.user.UserRegistered;
import io.vlingo.xoom.auth.model.permission.PermissionProvisioned;
import io.vlingo.xoom.auth.model.tenant.TenantActivated;
import io.vlingo.xoom.auth.model.role.RolePermissionDetached;
import io.vlingo.xoom.auth.model.user.UserCredentialReplaced;
import io.vlingo.xoom.auth.model.role.GroupAssignedToRole;
import io.vlingo.xoom.auth.model.permission.PermissionDescriptionChanged;
import io.vlingo.xoom.auth.model.permission.PermissionConstraintReplacementEnforced;
import io.vlingo.xoom.auth.model.user.UserCredentialRemoved;
import io.vlingo.xoom.auth.model.permission.PermissionConstraintForgotten;
import io.vlingo.xoom.auth.model.role.UserAssignedToRole;
import io.vlingo.xoom.auth.model.group.GroupDescriptionChanged;
import io.vlingo.xoom.auth.model.tenant.TenantSubscribed;
import io.vlingo.xoom.auth.model.group.UserAssignedToGroup;
import io.vlingo.xoom.auth.model.tenant.TenantNameChanged;
import io.vlingo.xoom.auth.model.group.GroupProvisioned;
import io.vlingo.xoom.auth.model.role.UserUnassignedFromRole;
import io.vlingo.xoom.auth.model.user.UserActivated;
import io.vlingo.xoom.auth.model.role.RoleProvisioned;
import io.vlingo.xoom.auth.model.group.GroupAssignedToGroup;
import io.vlingo.xoom.auth.model.role.RoleDescriptionChanged;
import io.vlingo.xoom.auth.model.user.UserProfileReplaced;
import io.vlingo.xoom.auth.model.user.UserCredentialAdded;
import io.vlingo.xoom.auth.model.user.UserDeactivated;
import io.vlingo.xoom.auth.model.role.RolePermissionAttached;
import io.vlingo.xoom.auth.model.permission.PermissionConstraintEnforced;
import io.vlingo.xoom.auth.model.group.GroupUnassignedFromGroup;
import io.vlingo.xoom.auth.model.tenant.TenantDescriptionChanged;
import io.vlingo.xoom.auth.model.group.UserUnassignedFromGroup;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/projections#dispatcher-and-projectiondispatcher">
 *   Dispatcher and ProjectionDispatcher
 * </a>
 */
public class ExchangeDispatcher implements Dispatcher<Dispatchable<Entry<String>, State<String>>>, ConfirmDispatchedResultInterest {
  private static final Logger logger = LoggerFactory.getLogger(ExchangeDispatcher.class);

  private DispatcherControl control;
  private final List<Exchange> producerExchanges;
  private final Map<String, Set<String>> eventsByExchangeName = new HashMap<>();

  public ExchangeDispatcher(final Exchange ...producerExchanges) {
    this.eventsByExchangeName.put("xoom-auth-topic", new HashSet<>());
    this.eventsByExchangeName.get("xoom-auth-topic").add(RolePermissionAttached.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserUnassignedFromRole.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserRegistered.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(TenantDeactivated.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(PermissionProvisioned.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(TenantDescriptionChanged.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserAssignedToRole.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserAssignedToGroup.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserCredentialRemoved.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(PermissionConstraintEnforced.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(GroupAssignedToGroup.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(TenantNameChanged.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserCredentialReplaced.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(GroupAssignedToRole.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(GroupUnassignedFromRole.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(GroupDescriptionChanged.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(GroupUnassignedFromGroup.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(RoleProvisioned.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserUnassignedFromGroup.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(GroupProvisioned.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(TenantActivated.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(PermissionConstraintForgotten.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserCredentialAdded.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserProfileReplaced.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(PermissionDescriptionChanged.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserDeactivated.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(RoleDescriptionChanged.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(TenantSubscribed.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(UserActivated.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(RolePermissionDetached.class.getCanonicalName());
    this.eventsByExchangeName.get("xoom-auth-topic").add(PermissionConstraintReplacementEnforced.class.getCanonicalName());
    this.producerExchanges = Arrays.asList(producerExchanges);
  }

  @Override
  public void dispatch(final Dispatchable<Entry<String>, State<String>> dispatchable) {
    logger.debug("Going to dispatch id {}", dispatchable.id());

    for (Entry<String> entry : dispatchable.entries()) {
      this.send(JsonSerialization.deserialized(entry.entryData(), entry.typed()));
    }

    this.control.confirmDispatched(dispatchable.id(), this);
  }

  @Override
  public void confirmDispatchedResultedIn(Result result, String dispatchId) {
      logger.debug("Dispatch id {} resulted in {}", dispatchId, result);
  }

  @Override
  public void controlWith(DispatcherControl control) {
    this.control = control;
  }

  private void send(final Object event) {
    this.findInterestedIn(event).forEach(exchange -> exchange.send(event));
  }

  private Stream<Exchange> findInterestedIn(final Object event) {
    final Set<String> exchangeNames =
          eventsByExchangeName.entrySet().stream().filter(exchange -> {
             final Set<String> events = exchange.getValue();
             return events.contains(event.getClass().getCanonicalName());
         }).map(Map.Entry::getKey).collect(Collectors.toSet());

    return this.producerExchanges.stream().filter(exchange -> exchangeNames.contains(exchange.name()));
  }

}
