package io.vlingo.xoom.auth.test;

import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.symbio.BaseEntry;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {

  public static void assertEventDispatched(final AccessSafely dispatcherAccess, final int sequence, final Class<?> expectedEvent) {
    assertEquals(sequence, (int) dispatcherAccess.readFrom("entriesCount"), String.format("Expected at least %d events", sequence));
    assertEquals(expectedEvent.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", sequence - 1)).typeName(), String.format("Expected the %d event in the sequence to be %s", sequence, expectedEvent.getName()));
  }
}
