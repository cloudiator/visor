#!/usr/bin/env bash

date=$(date --iso-8601=seconds)
version=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }'`
version=${version/SNAPSHOT/DEVELOP}

sed -i -e "s/TRAVIS_BUILD/$TRAVIS_BUILD_NUMBER/g" ci/bintray.json
sed -i -e "s/DATE/$date/g" ci/bintray.json
sed -i -e "s/VERSION/$version/g" ci/bintray.json
