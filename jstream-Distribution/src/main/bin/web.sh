#!/bin/bash
cd ${0%/*}/..
MAIN_HOME=.

source conf/jstream-env.sh
#JAVA10_HOME


#stop 通用 启动脚本 mainClass 为进程坚持程序 必须唯一且可靠 否则请修改pid获取办法
mainClass=com.jyb.controller.JettyServer


for jar in $MAIN_HOME/lib/*.jar;
do
    CLASSPATH=$CLASSPATH:$jar
done

if [ -z $GRAPHX_OPTS ]; then
  GRAPHX_OPTS=-Xmx1G
fi

#HADOOP_CONF_DIR=/etc/hadoop/conf
#
exec java $GRAPHX_OPTS -cp lib/*: -Dconfig=conf/jstream.properties  $mainClass 18080 "$@"


nohup $cmd > ${0%/*}/../logs/web.log 2>&1 &
#echo "Starting $mainClass,the pid is "$!
