#!/usr/bin/env bash


bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
./perfTest $bin/../configs/campaigns/campaign-perf.properties
 