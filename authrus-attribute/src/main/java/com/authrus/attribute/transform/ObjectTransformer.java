package com.authrus.attribute.transform;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ObjectTransformer {

   private final Map<Class, ObjectTransform> cache;
   private final Set<Class> error;

   public ObjectTransformer() {
      this.cache = new ConcurrentHashMap<Class, ObjectTransform>();
      this.error = new CopyOnWriteArraySet<Class>();
   }

   public boolean valid(Class type) {
      return lookup(type) != null;
   }

   public ObjectTransform lookup(Class type) {
      if (!error.contains(type)) {
         ObjectTransform transform = cache.get(type);

         if (transform != null) {
            return transform;
         }
         return match(type);
      }
      return null;
   }

   private ObjectTransform match(Class type) {
      ObjectTransform transform = matchType(type);

      if (transform != null) {
         cache.put(type, transform);
      } else {
         error.add(type);
      }
      return transform;
   }

   private ObjectTransform matchType(Class type) {
      if (type == int.class) {
         return new IdentityTransform();
      }
      if (type == boolean.class) {
         return new IdentityTransform();
      }
      if (type == long.class) {
         return new IdentityTransform();
      }
      if (type == double.class) {
         return new IdentityTransform();
      }
      if (type == float.class) {
         return new IdentityTransform();
      }
      if (type == short.class) {
         return new IdentityTransform();
      }
      if (type == byte.class) {
         return new IdentityTransform();
      }
      if (type == char.class) {
         return new IdentityTransform();
      }
      return matchPackage(type);
   }

   private ObjectTransform matchPackage(Class type) {
      String name = type.getName();

      if (name.startsWith("java.lang")) {
         return matchLanguage(type);
      }
      if (name.startsWith("java.util")) {
         return matchUtility(type);
      }
      if (name.startsWith("java.net")) {
         return matchURL(type);
      }
      if (name.startsWith("java.io")) {
         return matchFile(type);
      }
      if (name.startsWith("java.sql")) {
         return matchSQL(type);
      }
      if (name.startsWith("java.math")) {
         return matchMath(type);
      }
      return matchEnum(type);
   }

   private ObjectTransform matchEnum(Class type) {
      Class parent = type.getSuperclass();

      if (parent != null) {
         if (parent.isEnum()) {
            return new IdentityTransform();
         }
         if (type.isEnum()) {
            return new IdentityTransform();
         }
      }
      return null;
   }

   private ObjectTransform matchLanguage(Class type) {
      if (type == Boolean.class) {
         return new IdentityTransform();
      }
      if (type == Integer.class) {
         return new IdentityTransform();
      }
      if (type == Long.class) {
         return new IdentityTransform();
      }
      if (type == Double.class) {
         return new IdentityTransform();
      }
      if (type == Float.class) {
         return new IdentityTransform();
      }
      if (type == Short.class) {
         return new IdentityTransform();
      }
      if (type == Byte.class) {
         return new IdentityTransform();
      }
      if (type == Character.class) {
         return new IdentityTransform();
      }
      if (type == String.class) {
         return new IdentityTransform();
      }
      if (type == Class.class) {
         return new ClassTransform();
      }
      return null;
   }

   private ObjectTransform matchMath(Class type) {
      if (type == BigDecimal.class) {
         return new BigDecimalTransform();
      }
      if (type == BigInteger.class) {
         return new BigIntegerTransform();
      }
      return null;
   }

   private ObjectTransform matchUtility(Class type) {
      if (type == Date.class) {
         return new DateTransform(type);
      }
      if (type == Locale.class) {
         return new LocaleTransform();
      }
      if (type == Currency.class) {
         return new CurrencyTransform();
      }
      if (type == GregorianCalendar.class) {
         return new GregorianCalendarTransform(type);
      }
      if (type == TimeZone.class) {
         return new TimeZoneTransform();
      }
      if (type == AtomicInteger.class) {
         return new AtomicIntegerTransform();
      }
      if (type == AtomicLong.class) {
         return new AtomicLongTransform();
      }
      return null;
   }

   private ObjectTransform matchSQL(Class type) {
      if (type == Time.class) {
         return new DateTransform(type);
      }
      if (type == java.sql.Date.class) {
         return new DateTransform(type);
      }
      if (type == Timestamp.class) {
         return new DateTransform(type);
      }
      return null;
   }

   private ObjectTransform matchFile(Class type) {
      if (type == File.class) {
         return new FileTransform();
      }
      return null;
   }

   private ObjectTransform matchURL(Class type) {
      if (type == URL.class) {
         return new URLTransform();
      }
      return null;
   }
}
