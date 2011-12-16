#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

rep=100
if [ -z "$1" ]
then
  echo "no input supplied, defaulting replication factor = 100, e.g. 100*15000 = 1500000 docs"
else
  rep=$1
  numdocs=$rep*15000
  echo "replication factor = $1, e.g. $1*15000 = $numdocs"
fi

for (( c=1; c<=$rep; c++ )); do  cat $bin/../data/cars.json >> $bin/../data/test.json; done

