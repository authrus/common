package com.authrus.common.thread;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ThreadDumper {

   public String dumpThreads() {
      Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
      return generateDump(stackTraces);
   }

   public String dumpCurrentThread() {
      Thread currentThread = Thread.currentThread();
      StackTraceElement[] stackTrace = currentThread.getStackTrace();
      Map<Thread, StackTraceElement[]> stackTraces = Collections.singletonMap(currentThread, stackTrace);
      return generateDump(stackTraces);

   }

   private String generateDump(Map<Thread, StackTraceElement[]> stackTraces) {
      StringBuilder builder = new StringBuilder();

      builder.append("<pre>");
      builder.append("<b>Full Java thread dump</b>");
      builder.append("\n");

      Set<Thread> threads = stackTraces.keySet();

      for (Thread thread : threads) {
         StackTraceElement[] stackElements = stackTraces.get(thread);

         generateDescription(thread, builder);
         generateStackFrames(stackElements, builder);
      }
      builder.append("</pre>");
      return builder.toString();
   }

   private void generateStackFrames(StackTraceElement[] stackElements, StringBuilder builder) {
      for (StackTraceElement stackTraceElement : stackElements) {
         builder.append("    at ");
         builder.append(stackTraceElement);
         builder.append("\n");
      }
   }

   private void generateDescription(Thread thread, StringBuilder builder) {
      Thread.State threadState = thread.getState();
      String threadName = thread.getName();
      long threadId = thread.getId();

      builder.append("\n");
      builder.append("<b>");
      builder.append(threadName);
      builder.append("</b> Id=");
      builder.append(threadId);
      builder.append(" in ");
      builder.append(threadState);
      builder.append("\n");
   }
}
