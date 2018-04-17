// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model;

import java.io.IOException;

public final class Properties {
  public static final Properties instance;

  private static final String propertiesFile = "/vlingo-auth.properties";

  static {
    instance = open();
  }

  private final java.util.Properties properties;

  public static Properties open() {
    final java.util.Properties properties = new java.util.Properties();

    try {
      properties.load(Properties.class.getResourceAsStream(propertiesFile));
    } catch (IOException e) {
      throw new IllegalStateException("Must provide properties file on classpath: " + propertiesFile);
    }

    return new Properties(properties);
  }

  static Properties openForTest(java.util.Properties properties) {
    return new Properties(properties);
  }

  public final int cryptoArgon2MaxDuration() {
    final int maxDuration = getInteger("crypto.argon2.max.duration", 1000);
    return maxDuration;
  }

  public final int cryptoArgon2MemoryCost() {
    final int memoryCost = getInteger("crypto.argon2.memory.cost", 65536);
    return memoryCost;
  }

  public final int cryptoArgon2Parallelism() {
    final int parallelism = getInteger("crypto.argon2.parallelism", 1);
    return parallelism;
  }

  public final int cryptoScrypt_N_costFactor() {
    final int N = getInteger("crypto.scrypt.N.cost.factor", 16384);
    return N;
  }

  public final int cryptoScrypt_r_Blocksize() {
    final int r = getInteger("crypto.scrypt.r.blocksize", 8);
    return r;
  }

  public final int cryptoScrypt_p_parallelization() {
    final int p = getInteger("crypto.scrypt.p.parallelization", 1);
    return p;
  }

  public final Boolean getBoolean(final String key, final Boolean defaultValue) {
    final String value = getString(key, defaultValue.toString());
    return Boolean.parseBoolean(value);
  }

  public final Integer getInteger(final String key, final Integer defaultValue) {
    final String value = getString(key, defaultValue.toString());
    return Integer.parseInt(value);
  }

  public final String getString(final String key, final String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  private Properties(java.util.Properties properties) {
    this.properties = properties;
  }
}
