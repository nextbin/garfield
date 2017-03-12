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
    main_class='com.latios.garfield.cli.WatchingCli'
else
    main_class=$1
fi

echo "main class: $main_class"
java -cp ${jar} ${main_class} $@