// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
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

  public java.util.Properties properties() {
    return properties;
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
