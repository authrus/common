package com.authrus.common.chart;

public enum ChartType {
   DOT(true, false, false), 
   LINE(false, true, false), 
   LINE_DOT(true, true, false), 
   TIME(false, true, true), 
   TIME_DOT(true, true, true);

   private final boolean line;
   private final boolean time;
   private final boolean dot;

   private ChartType(boolean dot, boolean line, boolean time) {
      this.line = line;
      this.time = time;
      this.dot = dot;
   }

   public boolean isDot() {
      return dot;
   }

   public boolean isHistorical() {
      return time;
   }

   public boolean isLine() {
      return line;
   }

   public boolean isTime() {
      return time;
   }
}
