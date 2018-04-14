// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

public final class Credential implements Comparable<Credential> {
  public enum Type { VLINGO, OATH }

  public final String authority;
  public final String id;
  public final String secret;
  public final Type type;

  public static Credential oauthCredentialFrom(final String authority, final String id, final String secret) {
    return new Credential(authority, id, secret, Type.OATH);
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

  public Type type() {
    return type;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != Credential.class) {
      return false;
    }

    final Credential otherCredential = (Credential) other;

    return this.authority.equals(otherCredential.authority) && this.id.equals(otherCredential.id) && this.type == otherCredential.type;
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
