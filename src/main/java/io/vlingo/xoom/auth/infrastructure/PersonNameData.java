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

public class PersonNameData {

  public final String given;
  public final String family;
  public final String second;

  public static PersonNameData from(final PersonName personName) {
    return from(personName.given, personName.family, personName.second);
  }

  public static PersonNameData from(final String given, final String family, final String second) {
    return new PersonNameData(given, family, second);
  }

  public static Set<PersonNameData> from(final Set<PersonName> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptySet() : correspondingObjects.stream().map(PersonNameData::from).collect(Collectors.toSet());
  }

  public static List<PersonNameData> from(final List<PersonName> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptyList() : correspondingObjects.stream().map(PersonNameData::from).collect(Collectors.toList());
  }

  private PersonNameData (final String given, final String family, final String second) {
    this.given = given;
    this.family = family;
    this.second = second;
  }

  public PersonName toPersonName() {
    return PersonName.from(given, family, second);
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
