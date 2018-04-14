package io.vlingo.auth.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConstraintTest {

  @Test
  public void testThatConstraintHasCorrectValues() {
    final Constraint constraint = Constraint.of("Test", "12", "A test constraint with value of 12.");

    assertEquals("Test", constraint.name);
    assertEquals("12", constraint.value);
    assertEquals("A test constraint with value of 12.", constraint.description);
  }
}
