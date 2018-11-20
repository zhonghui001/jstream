#!/bin/bash
cd ${0%/*}/..
MAIN_HOME=.

source conf/jstream-env.sh

#stop 通用 启动脚本 mainClass 为进程坚持程序 必须唯一且可靠 否则请修改pid获取办法
mainClass=com.jyb.main.JstreamServer

for jar in $MAIN_HOME/lib/*.jar;
do
    CLASSPATH=$CLASSPATH:$jar
done

for xml in $MAIN_HOME/conf/*.xml;
do
    CLASSPATH=$CLASSPATH:$xml
done

if [ -z $GRAPHX_OPTS ]; then
  GRAPHX_OPTS=-Xmx1G
fi


if [ -z $GRAPHX_OPTS ]; then
  GRAPHX_OPTS=-Xmx1G
fi

exec java $GRAPHX_OPTS -cp lib/*: -Dconfig=conf/jstream.properties  $mainClass "$@"
nohup $cmd > ${0%/*}/../logs/server.log 2>&1 &
