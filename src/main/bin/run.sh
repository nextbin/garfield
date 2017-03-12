#!/bin/bash
jar=`ls | awk '{print $1}' | grep jar | grep dependencies`

print(){
    echo 'Usage: sh run.sh <command>, where <command> is one of these:'
    echo '    WatchingCli     启动定时监测CLI'
}
if [ $# -eq 0 ]; then
    print
    exit 0
fi

if [ "$1" = 'WatchingCli' ]; then
    class='WatchingCli'
else
    class=$1
fi

echo "main class: $class"
java -cp ${jar} ${class} $@