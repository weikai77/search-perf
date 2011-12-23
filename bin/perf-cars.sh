#!/usr/bin/env bash


bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
./runPerfTest.sh $bin/../configs/perf-sensei.properties
 