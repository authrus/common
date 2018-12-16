package com.authrus.transport.trace;

import java.nio.channels.SelectableChannel;

/**
 * This is modeled after a DTrace probe that will fire when a specific
 * low level I/O event occurs on a socket. Probes such as this are
 * useful for determining exactly what is happening in the system.
 * 
 * @author Niall Gallagher
 */
public class TraceEvent implements Comparable<TraceEvent> {

   private final SelectableChannel channel;
   private final String thread;
   private final Object event;
   private final Object value;
   private final long sequence;
   private final long time;
   
   public TraceEvent(SelectableChannel channel, String thread, Object event, Object value, long sequence) {
      this.time = System.currentTimeMillis();
      this.sequence = sequence;
      this.channel = channel;
      this.thread = thread;
      this.event = event;
      this.value = value;
   }   
   
   public long getTime() {
      return time;
   }   

   public long getSequence() {
      return sequence;
   }   

   public Object getType() {
      return event;
   }

   public Object getValue() {
      return value;
   }

   public SelectableChannel getChannel() {
      return channel;
   }

   public String getThread() {
      return thread;
   }
   
   @Override
   public int compareTo(TraceEvent other) {
      if(time < other.time) {
         return -1;
      }
      if(time == other.time) {
         return 0;
      }
      return 1;
   }
   
   public String toString() {
      return String.format("%s for %s at %s", event, sequence, time);
   }   
}
