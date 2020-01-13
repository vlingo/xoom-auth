// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

public final class Credential implements Comparable<Credential> {
  public static final String VlingoPlatformAuthority = "vlingo-platform";

  public enum Type { VLINGO, OAUTH }

  public final String authority;  // Credential is an entity and authority is the unique id
  public final String id;
  public final String secret;
  public final Type type;

  public static boolean isOAuthType(final String credentialType) {
    return Type.OAUTH.name().equals(credentialType);
  }

  public static boolean isVlingoType(final String credentialType) {
    return Type.VLINGO.name().equals(credentialType);
  }

  public static Credential credentialFrom(String authority, String id, String secret, String type) {
    if (isVlingoType(type)) {
      return vlingoCredentialFrom(authority, id, secret);
    } else if (isOAuthType(type)) {
      return oauthCredentialFrom(authority, id, secret);
    }
    throw new IllegalArgumentException("Unknow credential type: " + type);
  }

  public static Credential oauthCredentialFrom(final String authority, final String id, final String secret) {
    return new Credential(authority, id, secret, Type.OAUTH);
  }

  public static Credential vlingoCredentialFrom(final String authority, final String id, final String secret) {
    return new Credential(authority, id, secret, Type.VLINGO);
  }

  public String authority() {
    return authority;
  }

  public String id() {
    return id;
  }

  public String secret() {
    return secret;
  }

  public boolean isOauth() {
    return this.type == Type.OAUTH;
  }

  public boolean isVlingo() {
    return this.type == Type.VLINGO;
  }

  public Type type() {
    return type;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != Credential.class) {
      return false;
    }

    return this.authority.equals(((Credential) other).authority);
  }

  @Override
  public int hashCode() {
    return 31 * (id.hashCode() + type.hashCode());
  }

  @Override
  public String toString() {
    return "Credential[authority=" + authority + " id=" + id + " type=" + type + "]";
  }

  private Credential(final String authority, final String id, final String secret, final Type type) {
    if (authority == null || authority.trim().isEmpty()) throw new IllegalArgumentException("Credential authority required.");
    this.authority = authority;
    
    if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("Credential id required.");
    this.id = id;
    
    if (secret == null || secret.trim().isEmpty()) throw new IllegalArgumentException("Credential secret required.");
    this.secret = secret;
    
    if (type == null) throw new IllegalArgumentException("Credential type required.");
    this.type = type;
  }

  @Override
  public int compareTo(final Credential other) {
    return (this.authority + ":" + this.id + ":" + this.type).compareTo(other.authority + ":" + other.id + ":" + other.type);
  }
}
