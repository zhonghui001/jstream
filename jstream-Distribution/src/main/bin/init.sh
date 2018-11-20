#!/bin/bash
cd ${0%/*}/..
MAIN_HOME=.

source conf/jstream-env.sh
echo $JSTREAM_HOME
if [ ! -d $JSTREAM_HOME/logs/  ];then
  mkdir $JSTREAM_HOME/logs/
fi

ln -s $JSTREAM_HOME/logs/ $JSTREAM_HOME/webapp/logs
