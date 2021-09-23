package io.vlingo.xoom.auth.model.value;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Credential {

  public final String authority;
  public final String id;
  public final String secret;
  public final String type;

  public static Credential from(final String authority, final String id, final String secret, final String type) {
    return new Credential(authority, id, secret, type);
  }

  private Credential (final String authority, final String id, final String secret, final String type) {
    this.authority = authority;
    this.id = id;
    this.secret = secret;
    this.type = type;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(31, 17)
              .append(authority)
              .append(id)
              .append(secret)
              .append(type)
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
    Credential another = (Credential) other;
    return new EqualsBuilder()
              .append(this.authority, another.authority)
              .append(this.id, another.id)
              .append(this.secret, another.secret)
              .append(this.type, another.type)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("authority", authority)
              .append("id", id)
              .append("secret", secret)
              .append("type", type)
              .toString();
  }
}
