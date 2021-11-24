package io.vlingo.xoom.auth.infrastructure;

import io.vlingo.xoom.auth.infrastructure.persistence.UserView;
import io.vlingo.xoom.auth.model.value.PersonName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PersonNameData {

  public final String given;
  public final String family;
  public final String second;

  public static PersonNameData from(final PersonName personName) {
    if (personName == null) {
      return PersonNameData.empty();
    } else {
      return from(personName.given, personName.family, personName.second);
    }
  }

  public static PersonNameData from(final UserView.PersonNameView name) {
    return new PersonNameData(name.given, name.family, name.second);
  }

  public static PersonNameData from(final String given, final String family, final String second) {
    return new PersonNameData(given, family, second);
  }

  public static Set<PersonNameData> fromAll(final Set<PersonName> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptySet() : correspondingObjects.stream().map(PersonNameData::from).collect(Collectors.toSet());
  }

  public static List<PersonNameData> fromAll(final List<PersonName> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptyList() : correspondingObjects.stream().map(PersonNameData::from).collect(Collectors.toList());
  }

  public static PersonNameData of(String given, String second, String family) {
    return from(given, family, second);
  }

  private PersonNameData (final String given, final String family, final String second) {
    this.given = given;
    this.family = family;
    this.second = second;
  }

  public PersonName toPersonName() {
    return PersonName.from(given, family, second);
  }

  public static PersonNameData empty() {
    return new PersonNameData(null, null, null);
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
    PersonNameData another = (PersonNameData) other;
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
