#!/bin/bash
echo "installing elasticsearch 0.18.5"
mvn install:install-file -Dfile=lib/elasticsearch-0.18.5.jar -DgroupId=elasticsearch -DartifactId=elasticsearch -Dversion=0.18.5 -Dpackaging=jar
