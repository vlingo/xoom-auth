package io.vlingo.xoom.auth.model.value;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class PersonName {

  public final String given;
  public final String family;
  public final String second;

  public static PersonName from(final String given, final String family, final String second) {
    return new PersonName(given, family, second);
  }

  private PersonName (final String given, final String family, final String second) {
    this.given = given;
    this.family = family;
    this.second = second;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(31, 17)
              .append(given)
              .append(family)
              .append(second)
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
    PersonName another = (PersonName) other;
    return new EqualsBuilder()
              .append(this.given, another.given)
              .append(this.family, another.family)
              .append(this.second, another.second)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("given", given)
              .append("family", family)
              .append("second", second)
              .toString();
  }
}
