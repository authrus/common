package com.authrus.common.util;

public class StackTrace {

   private final String currentTrace;

   public StackTrace() {
      this.currentTrace = generateStackTrace();
   }

   public static String generateStackTrace() {
      StringBuilder builder = new StringBuilder();
      generateDump(builder);
      return builder.toString();
   }

   private static void generateDump(StringBuilder builder) {
      Thread currentThread = Thread.currentThread();
      StackTraceElement[] stackElements = currentThread.getStackTrace();

      generateDescription(currentThread, builder);
      generateStackFrames(stackElements, builder);
   }

   private static void generateStackFrames(StackTraceElement[] stackElements, StringBuilder builder) {
      for (StackTraceElement stackTraceElement : stackElements) {
         builder.append("    at ");
         builder.append(stackTraceElement);
         builder.append("\n");
      }
   }

   private static void generateDescription(Thread thread, StringBuilder builder) {
      Thread.State threadState = thread.getState();
      String threadName = thread.getName();
      long threadId = thread.getId();

      builder.append("\n");
      builder.append(threadName);
      builder.append(" Id=");
      builder.append(threadId);
      builder.append(" in ");
      builder.append(threadState);
      builder.append("\n");
   }

   @Override
   public String toString() {
      return currentTrace;
   }

}
