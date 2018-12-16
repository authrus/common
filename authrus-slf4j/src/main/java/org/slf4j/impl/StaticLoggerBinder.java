package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {

   public static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

   public static StaticLoggerBinder getSingleton() {
      return SINGLETON;
   }

   private final ILoggerFactory factory;

   private StaticLoggerBinder() {
      this.factory = new LoggerAdapterFactory();
   }

   public ILoggerFactory getLoggerFactory() {
      return factory;
   }

   public String getLoggerFactoryClassStr() {
      return LoggerAdapterFactory.class.getName();
   }
}