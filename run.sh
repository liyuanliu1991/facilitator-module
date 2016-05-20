#!/bin/sh
process_id=`/bin/ps -fu $USER| grep "java" | grep -v "grep" | awk '{print $2}'`
echo $process_id
kill $process_id
git pull
mvn tomcat7:run
