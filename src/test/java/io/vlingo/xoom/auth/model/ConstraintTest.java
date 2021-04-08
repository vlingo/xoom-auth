package io.vlingo.xoom.auth.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.vlingo.xoom.auth.model.Constraint.Type;

public class ConstraintTest {

  @Test
  public void testThatConstraintHasCorrectIntegerValues() {
    final Constraint constraint = Constraint.of(Type.Integer, "Test", "12", "A test constraint with value of 12.");

    assertEquals(Type.Integer, constraint.type);
    assertEquals("Test", constraint.name);
    assertEquals("12", constraint.value);
    assertEquals("A test constraint with value of 12.", constraint.description);
  }

  @Test
  public void testThatConstraintHasCorrectDoubleValues() {
    final Constraint constraint = Constraint.of(Type.Double, "Test", "12.00", "A test constraint with value of 12.00.");

    assertEquals(Type.Double, constraint.type);
    assertEquals("Test", constraint.name);
    assertEquals("12.00", constraint.value);
    assertEquals("A test constraint with value of 12.00.", constraint.description);
  }

  @Test
  public void testThatConstraintHasCorrectBooleanValues() {
    final Constraint constraint = Constraint.of(Type.Boolean, "Test", "true", "A test constraint with value of true.");

    assertEquals(Type.Boolean, constraint.type);
    assertEquals("Test", constraint.name);
    assertEquals("true", constraint.value);
    assertEquals("A test constraint with value of true.", constraint.description);
  }

  @Test
  public void testThatConstraintHasCorrectStringValues() {
    final Constraint constraint = Constraint.of(Type.String, "Test", "Some kinda text.", "A test constraint with value of --text--.");

    assertEquals(Type.String, constraint.type);
    assertEquals("Test", constraint.name);
    assertEquals("Some kinda text.", constraint.value);
    assertEquals("Some kinda text.", "A test constraint with value of --text--.", constraint.description);
  }
}
