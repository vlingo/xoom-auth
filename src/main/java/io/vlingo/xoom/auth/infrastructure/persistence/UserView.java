package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.auth.model.value.Credential;
import io.vlingo.xoom.auth.model.value.PersonName;
import io.vlingo.xoom.auth.model.value.Profile;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserView {
  public final String id;
  public final String tenantId;
  public final String username;
  public final ProfileView profile;
  public final boolean active;
  public final Set<CredentialView> credentials;
  public final Set<Relation<UserId, RoleId>> roles;

  public static UserView empty() {
    return from(UserId.from(TenantId.from(""), ""), "", null, Collections.emptySet(), false, Collections.emptySet());
  }

  public static UserView from(final UserId userId, final String username, final ProfileView profile, final Set<CredentialView> credentials, final boolean active, final Set<Relation<UserId, RoleId>> roles) {
    return new UserView(userId, username, profile, credentials, active, roles);
  }

  public static UserView from(final UserId userId, final String username, final ProfileView profile, final CredentialView credential, final boolean active, final Set<Relation<UserId, RoleId>> roles) {
    return new UserView(userId, username, profile, Stream.of(credential).collect(Collectors.toSet()), active, roles);
  }

  private UserView(String id, String tenantId, String username, ProfileView profile, Set<CredentialView> credentials, boolean active, final Set<Relation<UserId, RoleId>> roles) {
    this.id = id;
    this.tenantId = tenantId;
    this.username = username;
    this.profile = profile;
    this.active = active;
    this.credentials = credentials;
    this.roles = roles;
  }

  private UserView(UserId userId, String username, ProfileView profile, Set<CredentialView> credentials, boolean active, final Set<Relation<UserId, RoleId>> roles) {
    this(userId.idString(), userId.tenantId.id, username, profile, credentials, active, roles);
  }

  public boolean isInRole(final RoleId roleId) {
    return roles.stream().filter(r -> r.right.equals(roleId)).findFirst().isPresent();
  }

  public static class CredentialView {
    public final String authority;
    public final String id;
    public final String secret;
    public final String type;

    public static CredentialView empty() {
      return new CredentialView(null, null, null, null);
    }

    public static CredentialView from(final Credential credential) {
      if (credential == null) {
        return CredentialView.empty();
      } else {
        return from(credential.authority, credential.id, credential.secret, credential.type.name());
      }
    }

    public static CredentialView from(final String authority, final String id, final String secret) {
      return from(authority, id, secret, null);
    }

    public static CredentialView from(final String authority, final String id, final String secret, final String type) {
      return new CredentialView(authority, id, secret, type);
    }

    public static Set<CredentialView> fromAll(final Set<Credential> correspondingObjects) {
      return correspondingObjects == null ? Collections.emptySet() : correspondingObjects.stream().map(CredentialView::from).collect(Collectors.toSet());
    }

    private CredentialView(String authority, String id, String secret, String type) {
      this.authority = authority;
      this.id = id;
      this.secret = secret;
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CredentialView)) return false;
      CredentialView that = (CredentialView) o;
      return Objects.equals(authority, that.authority) && Objects.equals(id, that.id) && Objects.equals(secret, that.secret) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(authority, id, secret, type);
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("id", id)
              .append("authority", authority)
              .append("secret", secret)
              .append("type", type)
              .toString();
    }
  }

  public static class ProfileView {
    public final String emailAddress;
    public final PersonNameView name;
    public final String phone;

    public static ProfileView empty() {
      return new ProfileView(null, null, null);
    }

    public static ProfileView from(final Profile profile) {
      if (profile == null) {
        return ProfileView.empty();
      } else {
        final PersonNameView name = profile.name != null ? PersonNameView.from(profile.name) : null;
        return from(name, profile.emailAddress, profile.phone);
      }
    }

    public static ProfileView from(final PersonNameView name, final String emailAddress, final String phone) {
      return new ProfileView(emailAddress, name, phone);
    }

    public static ProfileView from(final String emailAddress, final PersonNameView name, final String phone) {
      return new ProfileView(emailAddress, name, phone);
    }

    private ProfileView(final String emailAddress, final PersonNameView name, final String phone) {
      this.emailAddress = emailAddress;
      this.name = name;
      this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ProfileView)) return false;
      ProfileView that = (ProfileView) o;
      return Objects.equals(emailAddress, that.emailAddress) && Objects.equals(name, that.name) && Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
      return Objects.hash(emailAddress, name, phone);
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

  public static class PersonNameView {
    public final String given;
    public final String family;
    public final String second;

    public static PersonNameView empty() {
      return new PersonNameView(null, null, null);
    }

    public static PersonNameView from(final PersonName personName) {
      if (personName == null) {
        return PersonNameView.empty();
      } else {
        return from(personName.given, personName.family, personName.second);
      }
    }

    public static PersonNameView from(final String given, final String family, final String second) {
      return new PersonNameView(given, family, second);
    }
    
    private PersonNameView(final String given, final String family, final String second) {
      this.given = given;
      this.family = family;
      this.second = second;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof PersonNameView)) return false;
      PersonNameView that = (PersonNameView) o;
      return Objects.equals(given, that.given) && Objects.equals(family, that.family) && Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
      return Objects.hash(given, family, second);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserView)) return false;
    UserView userView = (UserView) o;
    return active == userView.active && Objects.equals(id, userView.id) && Objects.equals(tenantId, userView.tenantId) && Objects.equals(username, userView.username) && Objects.equals(profile, userView.profile) && Objects.equals(credentials, userView.credentials);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, tenantId, username, profile, active, credentials);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("id", id)
            .append("tenantId", tenantId)
            .append("username", username)
            .append("profile", profile)
            .append("active", active)
            .append("credentials", credentials)
            .toString();
  }
}
