package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.lattice.model.projection.ProjectionControl;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.dispatch.ConfirmDispatchedResultInterest;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CountingDispatcherControl implements DispatcherControl {
  private AccessSafely access;
  private final Map<String, Integer> confirmations = new ConcurrentHashMap<>();

  public AccessSafely afterCompleting(final int times) {
    access = AccessSafely.afterCompleting(times);
    access.writingWith("confirmations", (String projectionId) -> {
      final int count = confirmations.getOrDefault(projectionId, 0);
      confirmations.put(projectionId, count + 1);
    });
    access.readingWith("confirmations", () -> confirmations);
    return access;
  }

  @Override
  public void confirmDispatched(final String dispatchId, final ConfirmDispatchedResultInterest interest) {
    access.writeUsing("confirmations", dispatchId);
    interest.confirmDispatchedResultedIn(Result.Success, dispatchId);
  }

  @Override
  public void dispatchUnconfirmed() {
  }

  @Override
  public void stop() {
  }
}