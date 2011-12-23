#!/usr/bin/env bash

#usage="Usage: start-perf.sh <conf-file>"

# if no args specified, show usage
#if [ $# -le 3 ]; then
#  echo $usage
#  exit 1
#fi

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
rm perf-result
rm report.log
lib=$bin/../target/dependency
classes=$bin/../target/classes
CLASSPATH=$lib/*:$classes/:.
# HEAP_OPTS="-Xmx4096m -Xms2048m -XX:NewSize=1024m" # -d64 for 64-bit awesomeness
# HEAP_OPTS="-Xmx1024m -Xms512m -XX:NewSize=128m"
# HEAP_OPTS="-Xmx512m -Xms256m -XX:NewSize=64m"
# GC_OPTS="-verbosegc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+UseConcMarkSweepGC -XX:+UseParNewGC"
# JAVA_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=1044,server=y,suspend=y"
#GC_OPTS="-XX:+UseConcMarkSweepGC -XX:+UseParNewGC"
#JAVA_OPTS="-server -d64"
#JMX_OPTS="-Djava.rmi.server.hostname=$IP -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=18889 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

MAIN_CLASS="com.linkedin.searchperf.common.launcher.PerformanceLauncher"
echo $CLASSPATH

pushd .
cd ..
echo The performanceResult.txt file would be created in the parent dir
java $JAVA_OPTS $JMX_OPTS $HEAP_OPTS $GC_OPTS $JAVA_DEBUG -classpath $CLASSPATH   $MAIN_CLASS $1
 