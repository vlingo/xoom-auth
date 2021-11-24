package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Address;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComplexAddressTest {

  @Test
  public void testItIsConvertedToLongId() {
    final Function<ComplexId, String> transformer = (ComplexId id) -> String.format("%s:%s", id.prefix, id.id);

    assertTrue((new ComplexAddress<>(new ComplexId("ABC", 123L), transformer)).id() > 0);
    assertTrue((new ComplexAddress<>(new ComplexId("ABC", 132L), transformer)).id() > 0);
    assertTrue((new ComplexAddress<>(new ComplexId("ABC", Long.MAX_VALUE), transformer)).id() > 0);
  }

  @Test
  public void testItTransformsTheIdToString() {
    final Address address = new ComplexAddress<>(
            new ComplexId("ABC", 123L),
            (ComplexId id) -> String.format("%s:%s", id.prefix, id.id)
    );

    assertEquals(String.valueOf(address.id()), address.idString());
    assertEquals(String.valueOf(address.idSequence()), address.idSequenceString());
  }

  @Test
  public void testCastsTheIdToTheRequestedType() {
    final ComplexId complexId = new ComplexId("ABC", 123L);
    final Address address = new ComplexAddress<>(
            complexId,
            (ComplexId id) -> String.format("%d", id.id)
    );

    assertEquals(complexId, address.<ComplexId>idTyped());
  }

  @Test
  public void testItIsDistributable() {
    final Address address = new ComplexAddress<>(
            new ComplexId("ABC", 123L),
            (ComplexId id) -> String.format("%d", id.id)
    );

    assertTrue(address.isDistributable());
  }

  @Test
  public void testItHasAName() {
    final Address address = new ComplexAddress<>(
            new ComplexId("ABC", 123L),
            (ComplexId id) -> String.format("%d", id.id)
    );

    assertEquals("complex", address.name());
  }

  private class ComplexId {
    public final String prefix;
    public final Long id;

    public ComplexId(String prefix, Long id) {
      this.prefix = prefix;
      this.id = id;
    }
  }
}
