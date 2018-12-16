package com.authrus.transport.trace;

import java.nio.channels.SelectableChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceLogger implements TraceListener {

   private static final Logger LOG = LoggerFactory.getLogger(TraceLogger.class);

   private final ObjectFormatter formatter;

   public TraceLogger() {
      this.formatter = new ObjectFormatter();
   }

   @Override
   public void onEvent(TraceEvent event) {
      long sequence = event.getSequence();
      Object type = event.getType();
      Object value = event.getValue();
      String thread = event.getThread();
      String text = formatter.format(value);
      SelectableChannel channel = event.getChannel();

      LOG.info(sequence + " [" + channel + "] " + thread + ": " + type + " -> " + text);

   }
}
