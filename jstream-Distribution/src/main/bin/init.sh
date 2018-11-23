#!/bin/bash
cd ${0%/*}/..
MAIN_HOME=.

source conf/jstream-env.sh
echo $JSTREAM_HOME
if [ ! -d $JSTREAM_HOME/logs/  ];then
  mkdir $JSTREAM_HOME/logs/
fi

ln -s $JSTREAM_HOME/logs/ $JSTREAM_HOME/webapp/logs


#2 上传配置文件至ck中
$HADOOP_HOME/bin/hadoop fs -mkdir -p $SPARK_CHECKPOINT_HOME
$HADOOP_HOME/bin/hadoop fs -put $JSTREAM_HOME/conf/jstream.properties $SPARK_CHECKPOINT_HOME

