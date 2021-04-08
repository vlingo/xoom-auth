// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infra.resource;

import io.vlingo.xoom.auth.model.Constraint;

public final class ConstraintData {
  public final String description;
  public final String name;
  public final String type;
  public final String value;

  public static ConstraintData from(final String type, final String name, final String value, final String description) {
    return new ConstraintData(type, name, value, description);
  }

  public static ConstraintData from(final Constraint constraint) {
    return new ConstraintData(constraint.type.name(), constraint.name, constraint.value, constraint.description);
  }

  public ConstraintData(final String type, final String name, final String value, final String description) {
    this.type = type;
    this.name = name;
    this.value = value;
    this.description = description;
  }
}
