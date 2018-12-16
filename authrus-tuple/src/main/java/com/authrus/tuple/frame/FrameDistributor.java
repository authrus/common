package com.authrus.tuple.frame;

import java.util.List;

import org.simpleframework.transport.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameDistributor implements FrameTracer {
   
   private static final Logger LOG = LoggerFactory.getLogger(FrameDistributor.class);

   private final List<FrameTracer> tracers;
   
   public FrameDistributor(List<FrameTracer> tracers) {
      this.tracers = tracers;
   }

   @Override
   public void onConnect(Session session, Channel channel) {
      for(FrameTracer tracer : tracers) {
         try {
            tracer.onConnect(session, channel);
         } catch(Exception e) {
            LOG.info("Error distributing connect from " + session, e);
         }
      }
   }
   
   @Override
   public void onDispatch(Session session, Frame frame) {
      for(FrameTracer tracer : tracers) {
         try {
            tracer.onDispatch(session, frame);
         } catch(Exception e) {
            LOG.info("Error distributing message from " + session, e);
         }
      }
   }

   @Override
   public void onException(Session session, Exception cause) {
      for(FrameTracer tracer : tracers) {
         try {
            tracer.onException(session, cause);
         } catch(Exception e) {
            LOG.info("Error distributing exception from " + session, e);
         }
      }
   }
   
   @Override
   public void onSuccess(Session session, int sequence) {
      for(FrameTracer tracer : tracers) {
         try {
            tracer.onSuccess(session, sequence);
         } catch(Exception e) {
            LOG.info("Error distributing success from " + session, e);
         }
      }
   }
   
   @Override
   public void onFailure(Session session, int sequence) {
      for(FrameTracer tracer : tracers) {
         try {
            tracer.onSuccess(session, sequence);
         } catch(Exception e) {
            LOG.info("Error distributing failure from " + session, e);
         }
      }
   }
   
   @Override
   public void onHeartbeat(Session session) {
      for(FrameTracer tracer : tracers) {
         try {
            tracer.onHeartbeat(session);
         } catch(Exception e) {
            LOG.info("Error distributing heartbeat from " + session, e);
         }
      }
   }

   @Override
   public void onClose(Session session) {
      for(FrameTracer tracer : tracers) {
         try {
            tracer.onClose(session);
         } catch(Exception e) {
            LOG.info("Error distributing close from " + session, e);
         }
      }
   }
}
