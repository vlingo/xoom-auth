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

public class CredentialData {

  public final String authority;
  public final String id;
  public final String secret;
  public final String type;

  public static CredentialData from(final Credential credential) {
    if (credential == null) {
      return CredentialData.empty();
    } else {
      return from(credential.authority, credential.id, credential.secret, credential.type);
    }
  }

  public static CredentialData from(final String authority, final String id, final String secret) {
    return from(authority, id, secret, null);
  }

  public static CredentialData from(final String authority, final String id, final String secret, final String type) {
    return new CredentialData(authority, id, secret, type);
  }

  public static Set<CredentialData> fromAll(final Set<Credential> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptySet() : correspondingObjects.stream().map(CredentialData::from).collect(Collectors.toSet());
  }

  public static List<CredentialData> fromAll(final List<Credential> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptyList() : correspondingObjects.stream().map(CredentialData::from).collect(Collectors.toList());
  }

  private CredentialData (final String authority, final String id, final String secret, final String type) {
    this.authority = authority;
    this.id = id;
    this.secret = secret;
    this.type = type;
  }

  public Credential toCredential() {
    return Credential.from(authority, id, secret, type);
  }

  public static CredentialData empty() {
    return new CredentialData(null, null, null, null);
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
    CredentialData another = (CredentialData) other;
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
