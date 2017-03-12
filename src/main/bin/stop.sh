#!bin/bash
for i in `jps -lm | grep com.latios.garfield.cli.WatchingCli | awk '{print $1}'`
do
  echo "kill pid "${i}
  kill ${i}
done

echo 'after killing:'
jps -lm
