package io.vlingo.xoom.auth.test;

import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.symbio.BaseEntry;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Assertions {

  /**
   * Asserts the number of dispatched events and the type of the last dispatched event.
   * @param dispatcherAccess Access to dispatcher's state (entriesCount)
   * @param sequence the event sequence to assert on
   * @param expectedEvent the expected event type
   */
  public static void assertEventDispatched(final AccessSafely dispatcherAccess, final int sequence, final Class<?> expectedEvent) {
    assertEquals(sequence, (int) dispatcherAccess.readFrom("entriesCount"), String.format("Expected at least %d events", sequence));
    assertEquals(expectedEvent.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", sequence - 1)).typeName(), String.format("Expected the %d event in the sequence to be %s", sequence, expectedEvent.getName()));
  }

  /**
   * Makes assertions on an outcome of an asynchronous operation.
   * @param completes the eventual completion of an asynchronous operation
   * @param assertions the consumer of the completed operation that will be called to make assertions
   * @param <T> the type of the outcome
   */
  public static <T> void assertCompletes(final Completes<T> completes, final Consumer<T> assertions) {
    final T outcome = completes.await(2000);
    assertNotEquals(null, outcome);
    assertions.accept(outcome);
  }
}
