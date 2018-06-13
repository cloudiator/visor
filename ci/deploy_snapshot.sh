#!/bin/bash

# see https://coderwall.com/p/9b_lfq
# see https://github.com/google/guava/blob/master/util/deploy_snapshot.sh

set -e -u

if [ "$TRAVIS_REPO_SLUG" == "cloudiator/visor" ] && \
   [ "$TRAVIS_JDK_VERSION" == "oraclejdk8" ] && \
   [ "$TRAVIS_PULL_REQUEST" == "false" ] && \
   [ "$TRAVIS_BRANCH" == "master" ]; then
  echo "Publishing Maven snapshot..."

  mvn -q clean javadoc:aggregate deploy --settings="ci/settings.xml" -DskipTests=true

  echo "Maven snapshot published."

fi
