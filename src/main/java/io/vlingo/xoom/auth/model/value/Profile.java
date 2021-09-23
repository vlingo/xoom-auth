package io.vlingo.xoom.auth.model.value;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Profile {

  public final String emailAddress;
  public final PersonName name;
  public final String phone;

  public static Profile from(final String emailAddress, final PersonName name, final String phone) {
    return new Profile(emailAddress, name, phone);
  }

  private Profile (final String emailAddress, final PersonName name, final String phone) {
    this.emailAddress = emailAddress;
    this.name = name;
    this.phone = phone;
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
    Profile another = (Profile) other;
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
