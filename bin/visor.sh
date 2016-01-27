#!/bin/bash

VISOR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

java=java
if test -n "$JAVA_HOME"; then
    java="$JAVA_HOME/bin/java"
fi
exec "$java" -Djava.library.path=$VISOR_DIR/../lib/sigar -jar $VISOR_DIR/../lib/visor.jar "$@"
