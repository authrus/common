package com.authrus.common.process;

public class ProcessResource {

   private final Long uniqueKey;
   private final String origin;
   private final String owner;
   private final String thread;
   private final String name;
   private final long size;
   private final long time;

   public ProcessResource(Long uniqueKey, String name, String owner, String thread, String origin, long size) {
      this.time = System.currentTimeMillis();
      this.uniqueKey = uniqueKey;
      this.thread = thread;
      this.origin = origin;
      this.owner = owner;
      this.name = name;
      this.size = size;
   }

   public Long getUniqueKey() {
      return uniqueKey;
   }
   
   public String getAllocationThread() {
      return thread;
   }

   public String getResourceOwner() {
      return owner;
   }

   public String getResourceName() {
      return name;
   }

   public String getResourceOrigin() {
      return origin;
   }
   
   public long getAllocationTime() {
      return time;
   }

   public long getSizeEstimate() {
      return size;
   }
   
   @Override
   public String toString() {
      return String.format("name=%s, owner=%s, origin=%s", name, owner, origin);
   }
}
