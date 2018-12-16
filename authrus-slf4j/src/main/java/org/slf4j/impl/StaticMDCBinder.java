package org.slf4j.impl;

import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;

public class StaticMDCBinder {

   public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

   public static StaticMDCBinder getSingleton() {
      return SINGLETON;
   }

   private final NOPMDCAdapter adapter;

   private StaticMDCBinder() {
      this.adapter = new NOPMDCAdapter();
   }

   public MDCAdapter getMDCA() {
      return adapter;
   }

   public String getMDCAdapterClassStr() {
      return NOPMDCAdapter.class.getName();
   }
}