package io.vlingo.xoom.auth.infrastructure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.stream.Collectors;
import java.util.*;
import io.vlingo.xoom.auth.model.value.*;
import java.util.stream.Stream;
import io.vlingo.xoom.auth.model.user.UserState;

@SuppressWarnings("all")
public class UserRegistrationData {
  public final String id;
  public final String tenantId;
  public final String username;
  public final boolean active;
  public final Set<CredentialData> credentials = new HashSet<>();
  public final ProfileData profile;

  public static UserRegistrationData from(final UserState userState) {
    final Set<CredentialData> credentials = userState.credentials != null ? userState.credentials.stream().map(CredentialData::from).collect(java.util.stream.Collectors.toSet()) : new HashSet<>();
    final ProfileData profile = userState.profile != null ? ProfileData.from(userState.profile) : null;
    return from(userState.id, userState.tenantId, userState.username, userState.active, credentials, profile);
  }

  public static UserRegistrationData from(final String id, final String tenantId, final String username, final boolean active, final Set<CredentialData> credentials, final ProfileData profile) {
    return new UserRegistrationData(id, tenantId, username, active, credentials, profile);
  }

  public static UserRegistrationData from(String tenantId, String username, ProfileData profile, CredentialData credential, boolean active) {
    return from(null, tenantId, username, active, Stream.of(credential).collect(Collectors.toSet()), profile);
  }

  public static List<UserRegistrationData> fromAll(final List<UserState> states) {
    return states.stream().map(UserRegistrationData::from).collect(Collectors.toList());
  }

  public static UserRegistrationData empty() {
    return from(UserState.identifiedBy(""));
  }

  private UserRegistrationData(final String id, final String tenantId, final String username, final boolean active, final Set<CredentialData> credentials, final ProfileData profile) {
    this.id = id;
    this.tenantId = tenantId;
    this.username = username;
    this.active = active;
    this.credentials.addAll(credentials);
    this.profile = profile;
  }

  public CredentialData credentialOf(final String authority) {
    for (final CredentialData credential : credentials) {
      if (credential.authority.equals(authority)) {
        return credential;
      }
    }
    return null;
  }

  public UserState toUserState() {
    final PersonName name = PersonName.from(this.profile.name.given, this.profile.name.family, this.profile.name.second);
    final Profile profile = Profile.from(this.profile.emailAddress, name, this.profile.phone);
    return new UserState(id, tenantId, username, active, credentials.stream().map(CredentialData::toCredential).collect(java.util.stream.Collectors.toSet()), profile);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    UserRegistrationData another = (UserRegistrationData) other;
    return new EqualsBuilder()
              .append(this.id, another.id)
              .append(this.tenantId, another.tenantId)
              .append(this.username, another.username)
              .append(this.active, another.active)
              .append(this.credentials, another.credentials)
              .append(this.profile, another.profile)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("id", id)
              .append("tenantId", tenantId)
              .append("username", username)
              .append("active", active)
              .append("credentials", credentials)
              .append("profile", profile)
              .toString();
  }

}
