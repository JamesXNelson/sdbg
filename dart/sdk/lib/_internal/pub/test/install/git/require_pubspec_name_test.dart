// Copyright (c) 2012, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

library pub_tests;

import '../../descriptor.dart' as d;
import '../../test_pub.dart';

main() {
  initConfig();
  integration('requires the dependency to have a pubspec with a name '
      'field', () {
    ensureGit();

    d.git('foo.git', [
      d.libDir('foo'),
      d.pubspec({})
    ]).create();

    d.appDir({"foo": {"git": "../foo.git"}}).create();

    // TODO(nweiz): clean up this RegExp when either issue 4706 or 4707 is
    // fixed.
    pubInstall(error:
        'Package "foo"\'s pubspec.yaml file is '
        'missing the required "name" field (e.g. "name: foo").');
  });
}
