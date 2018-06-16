#!/bin/bash

# CMDVAR="-Djava.security.egd=file:/dev/./urandom","java -agentlib:jdwp=transport=dt_socket,address=0:8000,server=y,suspend=n -jar"
# CMDVAR="-Duser.timezone=GMT+08"
java $CMDVAR -jar ./cron-service-0.0.1-SNAPSHOT.jar