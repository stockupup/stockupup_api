打包命令（根目录下执行）：  
1.mvn clean  
2.mvn package

启动脚本（start.sh)  
#!/bin/bash
PROJECTNAME=stockupup
pid=`ps -ef |grep $PROJECTNAME |grep -v "grep" |awk '{print $2}'`

if [ $pid ];then
   echo "$PROJECTNAME is running and pid=$pid"
else
   echo "Start success to start $PROJECTNAME ..."

   nohup java -jar stockupup-v1.0.jar >> catalina.out 2>&1 &

fi


停止脚本(stop.sh)  
#!/bin/bash
PROJECTNAME=stockupup
pid=`ps -ef |grep $PROJECTNAME |grep -v "grep" |awk '{print $2}'`
if [ $pid ];then
	echo "$PROJECTNAME is running and pid=$pid"
kill -9 $pid
	if [[ $? -eq 0 ]];then
echo "success to stop $PROJECTNAME "
	else
echo "fail to stop $PROJECTNAME "
	fi
fi

