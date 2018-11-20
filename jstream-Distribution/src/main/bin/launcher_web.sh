#!/bin/bash
MAIN_HOME=$(cd `dirname $0`; pwd)/..


runCmd=$MAIN_HOME/bin/web.sh
pidFile=$MAIN_HOME/logs/web.pid
logFile=$MAIN_HOME/logs/web.log

mkdir -p $MAIN_HOME/logs
touch $pidFile
 function getPid()
 {
    #pid=$(pgrep -u `whoami` -f $mainClass)
    touch $pidFile
    pid=`cat $pidFile`
    if [ "$pid" != "" ]; then
        pid= ps ax | awk '{ print $1 }' | grep -e "^$pid$"
    fi
    #echo $pid
	return $pid;
 }


 function start()
 {
    pid=$(getPid)
    if [ "$pid" != "" ]; then
        echo "The app alereay,the pid is $pid"
    else
        nohup $runCmd > $logFile 2>&1 &
        echo $! > $pidFile
        echo "Starting the pid is $!"
    fi
	return 0;
 }

 function run()
 {
    pid=$(getPid)
    if [ "$pid" != "" ]; then
        echo "app alereay,the pid is $pid"
    else
        exec $runCmd "$@"
    fi

	return 0;
 }

 function stop()
 {
    pid=$(getPid)
    if [ "$pid" != "" ]; then
        echo "Stopping ,the pid is $pid"
        kill $pid
    else
        echo "no start app"
    fi
	return 0;
 }

if [ "$1" == "start" ] || [ "$1" == "START" ]; then
    start
elif [ "$1" == "stop" ] || [ "$1" == "STOP" ]; then
    stop
elif [ "$1" == "run" ] || [ "$1" == "RUN" ]; then
    run
elif [ "$1" == "restart" ] || [ "$1" == "RESTART" ]; then
    stop
    sleep 1
    start
else
    echo "未知命令"
fi

