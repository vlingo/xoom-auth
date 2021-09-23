package io.vlingo.xoom.auth.infrastructure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import io.vlingo.xoom.auth.model.value.*;

public class ConstraintData {

  public final String description;
  public final String name;
  public final String type;
  public final String value;

  public static ConstraintData from(final Constraint constraint) {
    if (constraint == null) {
      return ConstraintData.empty();
    } else {
      return from(constraint.description, constraint.name, constraint.type, constraint.value);
    }
  }

  public static ConstraintData from(final String description, final String name, final String type, final String value) {
    return new ConstraintData(description, name, type, value);
  }

  public static Set<ConstraintData> from(final Set<Constraint> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptySet() : correspondingObjects.stream().map(ConstraintData::from).collect(Collectors.toSet());
  }

  public static List<ConstraintData> from(final List<Constraint> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptyList() : correspondingObjects.stream().map(ConstraintData::from).collect(Collectors.toList());
  }

  private ConstraintData (final String description, final String name, final String type, final String value) {
    this.description = description;
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public Constraint toConstraint() {
    return Constraint.from(description, name, type, value);
  }

  public static ConstraintData empty() {
    return new ConstraintData(null, null, null, null);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(31, 17)
              .append(description)
              .append(name)
              .append(type)
              .append(value)
              .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Constraint another = (Constraint) other;
    return new EqualsBuilder()
              .append(this.description, another.description)
              .append(this.name, another.name)
              .append(this.type, another.type)
              .append(this.value, another.value)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("description", description)
              .append("name", name)
              .append("type", type)
              .append("value", value)
              .toString();
  }
}
