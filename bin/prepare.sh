#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

numdocs=3000000
if [ -z "$1" ]
then
  echo "no input supplied, defaulting to 3000000 docs"
else
  numdocs=$1
  echo "$numdocs docs"
fi

python $bin/expandData.py $bin/../data/cars.json $bin/../data/test.json $numdocs

