#!/bin/bash

#EDIT HERE
PROCESS_HOME=/apps/rp/current
PID_FILE=$PROCESS_HOME/lock/run.pid
#SHOULD BE NO NEED TO EDIT BELOW HERE

cd $PROCESS_HOME

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
         echo "Killing running service..."
         cat "/proc/$PID/cmdline"  | xargs -0 echo
         kill $PID
         sleep 2
         kill -9 $PID
         rm -f $PID_FILE
      else
         echo "Removing stale pid file $PID_FILE as pid $PID has been recycled."
         echo "Process has been running for $PS_TIME_DESCRIPTION and pid file was modified on $PID_FILE_MOD_DATE."
         rm -f $PID_FILE
      fi
   else
      echo "Removing stale pid file $PID_FILE as pid $PID is not running."
      rm -f $PID_FILE
   fi
else
   echo "No pid file $PID_FILE was found."
fi
      