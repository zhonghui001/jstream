#!/bin/bash
cd ${0%/*}/..
MAIN_HOME=.

source conf/jstream-env.sh

for jar in $MAIN_HOME/lib/*.jar;
do
    CLASSPATH=$CLASSPATH:$jar
done

exec java $GRAPHX_OPTS -cp lib/*: -Dconfig=conf/jsteam.properties  "$@"