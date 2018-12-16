package org.slf4j.impl;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

import com.authrus.log.LogLevel;
import com.authrus.log.LogManager;

class LoggerAdapter extends MarkerIgnoringBase {

   public LoggerAdapter(String name) {
      this.name = name;
   }

   public boolean isTraceEnabled() {
      return LogManager.accept(LogLevel.TRACE);
   }

   public boolean isDebugEnabled() {
      return LogManager.accept(LogLevel.DEBUG);
   }

   public boolean isInfoEnabled() {
      return LogManager.accept(LogLevel.INFO);
   }

   public boolean isWarnEnabled() {
      return LogManager.accept(LogLevel.WARN);
   }

   public boolean isErrorEnabled() {
      return LogManager.accept(LogLevel.ERROR);
   }

   public void trace(String message) {
      log(LogLevel.TRACE, message, null);
   }

   public void trace(String format, Object list) {
      log(LogLevel.TRACE, format, list);
   }

   public void trace(String format, Object message, Object list) {
      log(LogLevel.TRACE, format, message, list);
   }

   public void trace(String format, Object... list) {
      log(LogLevel.TRACE, format, list);
   }

   public void trace(String message, Throwable throwable) {
      log(LogLevel.TRACE, message, throwable);
   }

   public void debug(String message) {
      log(LogLevel.DEBUG, message, null);
   }

   public void debug(String format, Object list) {
      log(LogLevel.DEBUG, format, list);
   }

   public void debug(String format, Object message, Object list) {
      log(LogLevel.DEBUG, format, message, list);
   }

   public void debug(String format, Object... list) {
      log(LogLevel.DEBUG, format, list);
   }

   public void debug(String message, Throwable throwable) {
      log(LogLevel.DEBUG, message, throwable);
   }

   public void info(String message) {
      log(LogLevel.INFO, message, null);
   }

   public void info(String format, Object list) {
      log(LogLevel.INFO, format, list);
   }

   public void info(String format, Object message, Object list) {
      log(LogLevel.INFO, format, message, list);
   }

   public void info(String format, Object... list) {
      log(LogLevel.INFO, format, list);
   }

   public void info(String message, Throwable throwable) {
      log(LogLevel.INFO, message, throwable);
   }

   public void warn(String message) {
      log(LogLevel.WARN, message, null);
   }

   public void warn(String format, Object list) {
      log(LogLevel.WARN, format, list);
   }

   public void warn(String format, Object message, Object list) {
      log(LogLevel.WARN, format, message, list);
   }

   public void warn(String format, Object... list) {
      log(LogLevel.WARN, format, list);
   }

   public void warn(String message, Throwable throwable) {
      log(LogLevel.WARN, message, throwable);
   }

   public void error(String message) {
      log(LogLevel.ERROR, message, null);
   }

   public void error(String format, Object message) {
      log(LogLevel.ERROR, format, message);
   }

   public void error(String format, Object message, Object list) {
      log(LogLevel.ERROR, format, message, list);
   }

   public void error(String format, Object... list) {
      log(LogLevel.ERROR, format, list);
   }

   public void error(String message, Throwable throwable) {
      log(LogLevel.ERROR, message, throwable);
   }

   private void log(LogLevel level, String format, Object... list) {
      if (LogManager.accept(level)) {
         FormattingTuple tuple = MessageFormatter.arrayFormat(format, list);
         String message = tuple.getMessage();
         Throwable throwable = tuple.getThrowable();

         LogManager.log(level, name, message, throwable);
      }
   }
}