#!/bin/bash
#
# Builds two variants of the Buzz Android app for release
#
rm app/release/app-internal-release.aab app/release/app-public-release.aab
mkdir -p app/release
./gradlew :app:bundleRelease -Prelease
cp app/build/outputs/bundle/release/app-release.aab app/release/app-internal-release.aab
./gradlew :app:bundleRelease -Prelease -Ppublic
cp app/build/outputs/bundle/release/app-release.aab app/release/app-public-release.aab
