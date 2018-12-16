package com.authrus.attribute.transform;

import java.util.GregorianCalendar;

public class GregorianCalendarTransform<T extends GregorianCalendar> implements ObjectTransform<GregorianCalendar, Long> {

   private final Class<T> type;

   public GregorianCalendarTransform(Class<T> type) {
      this.type = type;
   }

   public synchronized GregorianCalendar toObject(Long value) throws Exception {
      GregorianCalendar calendar = type.newInstance();

      calendar.setTimeInMillis(value);
      return calendar;
   }

   public synchronized Long fromObject(GregorianCalendar calendar) throws Exception {
      return calendar.getTimeInMillis();
   }
}
