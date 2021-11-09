package io.vlingo.xoom.auth.infrastructure;

import io.vlingo.xoom.auth.model.value.Constraint;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ConstraintData {

  public final String type;
  public final String name;
  public final String value;
  public final String description;

  public static ConstraintData from(final String type, final String name,  final String value, final String description) {
    return new ConstraintData(type, name, value, description);
  }

  private ConstraintData(final String type, final String name, final String value, final String description) {
    this.description = description;
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public Constraint toConstraint() {
    return Constraint.from(Constraint.Type.valueOf(type), name, value, description);
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
    ConstraintData another = (ConstraintData) other;
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
