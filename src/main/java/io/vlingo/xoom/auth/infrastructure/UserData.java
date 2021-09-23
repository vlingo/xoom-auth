package io.vlingo.xoom.auth.infrastructure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.stream.Collectors;
import java.util.*;
import io.vlingo.xoom.auth.model.value.*;
import io.vlingo.xoom.auth.model.user.UserState;

@SuppressWarnings("all")
public class UserData {
  public final String id;
  public final String tenantId;
  public final String username;
  public final boolean active;
  public final Set<CredentialData> credentials = new HashSet<>();
  public final ProfileData profile;

  public static UserData from(final UserState userState) {
    final Set<CredentialData> credentials = userState.credentials != null ? userState.credentials.stream().map(CredentialData::from).collect(java.util.stream.Collectors.toSet()) : new HashSet<>();
    final ProfileData profile = userState.profile != null ? ProfileData.from(userState.profile) : null;
    return from(userState.id, userState.tenantId, userState.username, userState.active, credentials, profile);
  }

  public static UserData from(final String id, final String tenantId, final String username, final boolean active, final Set<CredentialData> credentials, final ProfileData profile) {
    return new UserData(id, tenantId, username, active, credentials, profile);
  }

  public static List<UserData> fromAll(final List<UserState> states) {
    return states.stream().map(UserData::from).collect(Collectors.toList());
  }

  public static UserData empty() {
    return from(UserState.identifiedBy(""));
  }

  private UserData (final String id, final String tenantId, final String username, final boolean active, final Set<CredentialData> credentials, final ProfileData profile) {
    this.id = id;
    this.tenantId = tenantId;
    this.username = username;
    this.active = active;
    this.credentials.addAll(credentials);
    this.profile = profile;
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
    UserData another = (UserData) other;
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
