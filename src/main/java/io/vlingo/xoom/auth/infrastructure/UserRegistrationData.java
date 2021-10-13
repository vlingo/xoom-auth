package io.vlingo.xoom.auth.infrastructure;

import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.auth.model.user.UserState;
import io.vlingo.xoom.auth.model.value.PersonName;
import io.vlingo.xoom.auth.model.value.Profile;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    return from(userState.userId, userState.username, profile, credentials, userState.active);
  }

  public static UserRegistrationData from(final UserId userId, final String username, final ProfileData profile, final Set<CredentialData> credentials, final boolean active) {
    return new UserRegistrationData(userId, username, profile, credentials, active);
  }

  public static UserRegistrationData from(final UserId userId, final String username, final ProfileData profile, final CredentialData credential, final boolean active) {
    return new UserRegistrationData(userId, username, profile, Stream.of(credential).collect(Collectors.toSet()), active);
  }

  public static List<UserRegistrationData> fromAll(final List<UserState> states) {
    return states.stream().map(UserRegistrationData::from).collect(Collectors.toList());
  }

  public static UserRegistrationData empty() {
    return from(UserState.identifiedBy(UserId.from(TenantId.from(""), "")));
  }

  private UserRegistrationData(final UserId userId, final String username, final ProfileData profile, final Set<CredentialData> credentials, final boolean active) {
    this.id = userId.idString();
    this.tenantId = userId.tenantId.idString();
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
    return new UserState(UserId.from(TenantId.from(tenantId), username), username, profile, credentials.stream().map(CredentialData::toCredential).collect(java.util.stream.Collectors.toSet()), active);
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
