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

public class ProfileData {

  public final String emailAddress;
  public final PersonNameData name;
  public final String phone;

  public static ProfileData from(final Profile profile) {
    if (profile == null) {
      return ProfileData.empty();
    } else {
      final PersonNameData name = profile.name != null ? PersonNameData.from(profile.name) : null;
      return from(name, profile.emailAddress, profile.phone);
    }
  }

  public static ProfileData from(final PersonNameData name, final String emailAddress, final String phone) {
    return new ProfileData(emailAddress, name, phone);
  }

  public static ProfileData from(final String emailAddress, final PersonNameData name, final String phone) {
    return new ProfileData(emailAddress, name, phone);
  }

  public static Set<ProfileData> fromAll(final Set<Profile> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptySet() : correspondingObjects.stream().map(ProfileData::from).collect(Collectors.toSet());
  }

  public static List<ProfileData> fromAll(final List<Profile> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptyList() : correspondingObjects.stream().map(ProfileData::from).collect(Collectors.toList());
  }

  private ProfileData (final String emailAddress, final PersonNameData name, final String phone) {
    this.emailAddress = emailAddress;
    this.name = name;
    this.phone = phone;
  }

  public Profile toProfile() {
    final PersonName name = PersonName.from(this.name.given, this.name.family, this.name.second);
    return Profile.from(emailAddress, name, phone);
  }

  public static ProfileData empty() {
    return new ProfileData(null, null, null);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(31, 17)
              .append(emailAddress)
              .append(name)
              .append(phone)
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
    ProfileData another = (ProfileData) other;
    return new EqualsBuilder()
              .append(this.emailAddress, another.emailAddress)
              .append(this.name, another.name)
              .append(this.phone, another.phone)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("emailAddress", emailAddress)
              .append("name", name)
              .append("phone", phone)
              .toString();
  }
}
