#!/usr/bin/env bash

set -e

# make release
./gradlew release

# upload artifacts to Bintray
./gradlew bintrayUpload
