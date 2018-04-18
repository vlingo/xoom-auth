// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.auth.model.crypto;

import io.vlingo.auth.model.Properties;

public interface Hasher {
  static Hasher defaultHasher() {
    switch (Properties.instance.cryptoType()) {
    case "argon2":
      return new Argon2Hasher(Properties.instance.cryptoArgon2MaxDuration(), Properties.instance.cryptoArgon2MemoryCost(), Properties.instance.cryptoArgon2Parallelism());
    case "scrypt":
      return new SCryptHasher(Properties.instance.cryptoScrypt_N_costFactor(), Properties.instance.cryptoScrypt_r_Blocksize(), Properties.instance.cryptoScrypt_p_parallelization());
    case "bcrypt":
      return new BCryptHasher();
    }
    throw new IllegalStateException("Crypto type is not defined.");
  }

  String hash(final String plainSecret);
  boolean verify(final String plainSecret, final String hashedSecret);
}
