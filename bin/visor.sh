#!/bin/sh
java=java
if test -n "$JAVA_HOME"; then
    java="$JAVA_HOME/bin/java"
fi
pushd `dirname $0` > /dev/null
SCRIPTPATH=`pwd`
exec "$java" -jar $SCRIPTPATH/../lib/visor.jar "$@"
