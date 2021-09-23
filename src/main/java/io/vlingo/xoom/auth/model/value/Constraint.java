package io.vlingo.xoom.auth.model.value;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Constraint {

  public final String description;
  public final String name;
  public final String type;
  public final String value;

  public static Constraint from(final String description, final String name, final String type, final String value) {
    return new Constraint(description, name, type, value);
  }

  private Constraint (final String description, final String name, final String type, final String value) {
    this.description = description;
    this.name = name;
    this.type = type;
    this.value = value;
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
