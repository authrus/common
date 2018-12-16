package com.authrus.common.thread;

import java.util.concurrent.ThreadFactory;

public class ThreadPoolFactory implements ThreadFactory {

   private final String prefix;

   public ThreadPoolFactory(Class type) {
      this(type.getSimpleName());
   }

   public ThreadPoolFactory(String prefix) {
      this.prefix = prefix;
   }

   @Override
   public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable);
      String name = formatName(thread);

      thread.setName(name);
      return thread;
   }

   private String formatName(Thread thread) {
      String name = thread.getName();

      if (name != null) {
         return String.format("%s: %s", prefix, name);
      }
      return prefix;
   }
}
