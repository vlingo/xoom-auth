package io.vlingo.xoom.auth.model.value;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Credential {

  public enum Type { XOOM, OAUTH }

  public final String authority;
  public final String id;
  public final String secret;
  public final Type type;

  public static Credential from(final String authority, final String id, final String secret, final String type) {
    if (null == type || isXoomType(type)) {
      return xoomCredentialFrom(authority, id, secret);
    } else if (isOAuthType(type)) {
      return oauthCredentialFrom(authority, id, secret);
    }
    throw new IllegalArgumentException(String.format("Unknown credential type: %s", type));
  }

  public static Credential xoomCredentialFrom(final String authority, final String id, final String secret) {
    return new Credential(authority, id, secret, Type.XOOM);
  }

  public static Credential oauthCredentialFrom(final String authority, final String id, final String secret) {
    return new Credential(authority, id, secret, Type.OAUTH);
  }

  private Credential(final String authority, final String id, final String secret, final Type type) {
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

  private static boolean isOAuthType(final String credentialType) {
    return Type.OAUTH.name().equals(credentialType);
  }

  private static boolean isXoomType(final String credentialType) {
    return Type.XOOM.name().equals(credentialType);
  }
}
