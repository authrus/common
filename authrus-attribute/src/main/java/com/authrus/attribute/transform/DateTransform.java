package com.authrus.attribute.transform;

import java.util.Date;

public class DateTransform<T extends Date> implements ObjectTransform<T, Long> {

   private final Class<T> type;

   public DateTransform(Class<T> type) {
      this.type = type;
   }

   public synchronized T toObject(Long value) throws Exception {
      T date = type.newInstance();

      date.setTime(value);
      return date;
   }

   public synchronized Long fromObject(T date) throws Exception {
      return date.getTime();
   }
}
