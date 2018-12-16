package org.slf4j.impl;

import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

public class StaticMarkerBinder implements MarkerFactoryBinder {

   public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

   public static StaticMarkerBinder getSingleton() {
      return SINGLETON;
   }

   private final IMarkerFactory factory;

   private StaticMarkerBinder() {
      this.factory = new BasicMarkerFactory();
   }

   public IMarkerFactory getMarkerFactory() {
      return factory;
   }

   public String getMarkerFactoryClassStr() {
      return BasicMarkerFactory.class.getName();
   }
}