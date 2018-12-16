package com.authrus.attribute.transform;

import java.util.TimeZone;

public class TimeZoneTransform implements ObjectTransform<TimeZone, String> {

   public TimeZone toObject(String zone) {
      return TimeZone.getTimeZone(zone);
   }

   public String fromObject(TimeZone zone) {
      return zone.getID();
   }
}
