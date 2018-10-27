#!/bin/bash

#EDIT HERE
PROCESS_JAR=gateway.jar
PROCESS_ENVIRONMENT=prod
PROCESS_CONFIGURATION_FILE=application-$PROCESS_ENVIRONMENT.yaml
PROCESS_MEMORY="-server -Xms64m -Xmx128m -verbose:gc -Xloggc:log/gc.log -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:InitiatingHeapOccupancyPercent=45 -XX:G1ReservePercent=20 -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:MaxPermSize=128m"
PROCESS_HOME=/apps/rp/current export PROCESS_HOME
PROCESS_OWNER=root
JAVA_HOME=/usr export JAVA_HOME
PID_FILE=$PROCESS_HOME/lock/run.pid
#no additional variables
#SHOULD BE NO NEED TO EDIT BELOW HERE

#change this is /etc/security/limits.conf
#
#dev hard nofile 64000
#dev soft nofile 64000
#
ulimit -n 64000
ulimit -s 64000

#make sure previous process not running
if [ -f $PID_FILE ]; then
   PID=$(cat $PID_FILE)
   PROC_FILE="/proc/$PID"

   if [ -d $PROC_FILE ]; then
      SYSTEM_TIME=$(date +%s)
      PID_FILE_MOD_TIME_SECONDS=$(stat -c "%X" $PID_FILE)
      PID_FILE_MOD_DATE=$(stat -c "%y" $PID_FILE)
      PS_TIME_ELAPSED=$(ps -o etime= -p $PID | sed "s/-/:/g" | sed "s/:0/:/g" | sed "s/^\s*0//")
      PS_TIME_PARTS=(${PS_TIME_ELAPSED//:/ })
      PS_TIME_PARTS_LENGTH="${#PS_TIME_PARTS[@]}"

      case $PS_TIME_PARTS_LENGTH in
      4)
         PS_TIME_DAYS=$(expr "${PS_TIME_PARTS[0]}" \* 86400)
         PS_TIME_HOURS=$(expr "${PS_TIME_PARTS[1]}" \* 3600)
         PS_TIME_MINUTES=$(expr "${PS_TIME_PARTS[2]}" \* 60)
         PS_TIME_SECONDS="${PS_TIME_PARTS[3]}"
         PS_TIME_DESCRIPTION="${PS_TIME_PARTS[0]} days ${PS_TIME_PARTS[1]} hours ${PS_TIME_PARTS[2]} minutes ${PS_TIME_PARTS[3]} seconds"
         ;;
      3)
         PS_TIME_DAYS=0
         PS_TIME_HOURS=$(expr "${PS_TIME_PARTS[0]}" \* 3600)
         PS_TIME_MINUTES=$(expr "${PS_TIME_PARTS[1]}" \* 60)
         PS_TIME_SECONDS="${PS_TIME_PARTS[2]}"
         PS_TIME_DESCRIPTION="${PS_TIME_PARTS[0]} hours ${PS_TIME_PARTS[1]} minutes ${PS_TIME_PARTS[2]} seconds"
         ;;
      2)
         PS_TIME_DAYS=0
         PS_TIME_HOURS=0
         PS_TIME_MINUTES=$(expr "${PS_TIME_PARTS[0]}" \* 60)
         PS_TIME_SECONDS="${PS_TIME_PARTS[1]}"
         PS_TIME_DESCRIPTION="${PS_TIME_PARTS[0]} minutes ${PS_TIME_PARTS[1]} seconds"
         ;;
      *)
         echo "Service status not known for $PID with 'ps -o etime= -p $PID' as '$PS_TIME_ELAPSED' cannot be parsed"
         exit 2
      esac

      #time in seconds ps shows it was running
      PS_TIME_TOTAL_SECONDS=$(expr $PS_TIME_SECONDS + $PS_TIME_MINUTES + $PS_TIME_HOURS + $PS_TIME_DAYS)
      PID_FILE_TIME_SINCE_MOD_SECONDS=$(expr $SYSTEM_TIME - $PID_FILE_MOD_TIME_SECONDS)

      #running time should be longer than the pid file always
      if (( $PS_TIME_TOTAL_SECONDS >= $PID_FILE_TIME_SINCE_MOD_SECONDS )); then
         echo "Service already running."
         echo "Process has been running for $PS_TIME_DESCRIPTION and pid file was modified on $PID_FILE_MOD_DATE."
         echo "Run bin/stop.sh if you wish to stop the existing process."
         exit 1
          else
         echo "Removing stale pid file $PID_FILE as pid $PID has been recycled."
         rm -f $PID_FILE
      fi
   else
      echo "Removing stale pid file $PID_FILE as pid $PID is not running."
      rm -f $PID_FILE
   fi
fi

CURRENT_USER=$(whoami)

if [ "$CURRENT_USER" != "$PROCESS_OWNER" ]; then
        echo "DO NOT RUN THIS AS $CURRENT_USER - USE THE service command!"
        exit 1
fi

PATH=$PROCESS_HOME/etc:$PATH export PATH
CLASSPATH=$PROCESS_HOME/etc:$PROCESS_HOME/lib/${PROCESS_JAR} export CLASSPATH

echo CLASSPATH=$CLASSPATH

#start the service

cd $PROCESS_HOME

for (( i = 1; i<10000; i++ ))
  do
        v="log/app.stdout"$i
        if [ -f $v ]
        then
                echo "$v exists"
        else
                mv log/app.stdout $v
                break
        fi
  done

JAVA_OPTS="-Djava.net.preferIPv4Stack=true ${PROCESS_MEMORY}"

HOSTNAME=`hostname -i`

echo "$JAVA_HOME/bin/java ${JAVA_OPTS} -cp $CLASSPATH -Dspring.profiles.active=${PROCESS_ENVIRONMENT} org.springframework.boot.loader.JarLauncher"
nohup $JAVA_HOME/bin/java ${JAVA_OPTS} -cp $CLASSPATH -Dspring.profiles.active=${PROCESS_ENVIRONMENT} org.springframework.boot.loader.JarLauncher 1> log/app.stdout 2> log/app.stderr &

PID=$!
echo $PID > $PID_FILE
echo service running: $PID
         