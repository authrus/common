package com.authrus.common.socket.throttle;

public interface ThrottleAlarm {
   void raiseInputAlarm(ThrottleEvent event);
   void raiseOutputAlarm(ThrottleEvent event);
}
