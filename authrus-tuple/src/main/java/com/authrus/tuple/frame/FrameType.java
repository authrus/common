package com.authrus.tuple.frame;

public enum FrameType {
   MESSAGE('M'), 
   HEARTBEAT('H'),
   RECEIPT('R'),
   QUERY('Q');

   public final int type;

   private FrameType(int type) {
      this.type = type;
   }

   public static FrameType resolveType(int type) {
      if (type == 'M') {
         return MESSAGE;
      }
      if (type == 'H') {
         return HEARTBEAT;
      }
      if (type == 'R') {
         return RECEIPT;
      }
      if (type == 'Q') {
         return QUERY;
      }      
      throw new IllegalArgumentException("No match for " + type);
   }
}
