#!/usr/bin/env bash

set -e

increment=$(echo ${1-minor} | awk '{print toupper($0)}')

case "$increment" in
"PATCH")
    param="incrementPatch"
    ;;
"MINOR")
    param="incrementMinor"
    ;;
"MAJOR")
    param="incrementMajor"
    ;;
"PRERELEASE")
    param="incrementPrerelease"
    ;;
*)
    echo "invalid value for increment: ${increment}"
    exit 1
    ;;
esac

# make release
./gradlew release -Prelease.versionIncrementer=${param}

# upload artifacts to Artifactory
./gradlew artifactoryPublish
