#!/bin/bash

# CMDVAR="-Djava.security.egd=file:/dev/./urandom","java -agentlib:jdwp=transport=dt_socket,address=0:8000,server=y,suspend=n -jar"
CMDVAR="-Duser.timezone=GMT+08"
java $CMDVAR -jar ./oiosaml-proxy-1.0.jar