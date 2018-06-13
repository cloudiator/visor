#!/usr/bin/env bash

date=$(date)

sed -i -e "s/TRAVIS_BUILD/$TRAVIS_BUILD_NUMBER/g" ci/bintray.json
sed -i -e "s/DATE/$date/g" ci/bintray.json
