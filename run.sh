#!/bin/bash
nohup java -Xms1g -Xmx2g -XX:+HeapDumpOnOutOfMemoryError -XX:OnOutOfMemoryError="kill -9 %p;./run.sh" -classpath "lib/*" -jar jalgoarena-judge-*.jar >/dev/null 2>&1 &
