package com.authrus.tuple.grid.record;

import static com.authrus.tuple.frame.FrameType.MESSAGE;

import org.simpleframework.transport.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.io.DataReader;
import com.authrus.tuple.frame.Frame;
import com.authrus.tuple.frame.FrameAdapter;
import com.authrus.tuple.frame.FrameType;
import com.authrus.tuple.frame.Session;

public class DeltaScanner extends FrameAdapter {
   
   private static final Logger LOG = LoggerFactory.getLogger(DeltaScanner.class);
   
   private final Cache<Session, DeltaInterceptor> interceptors;
   private final DeltaRecordListener listener;
   private final boolean enable;

   public DeltaScanner(DeltaRecordListener listener) {
      this(listener, false);
   }
   
   public DeltaScanner(DeltaRecordListener listener, boolean enable) {
      this.interceptors = new LeastRecentlyUsedCache<Session, DeltaInterceptor>();
      this.listener = listener;
      this.enable = enable;
   }   

   @Override
   public void onConnect(Session session, Channel channel) {
      if(enable) {
         DeltaInterceptor interceptor = new DeltaInterceptor(listener, session);
         
         if(session != null) {
            interceptors.cache(session, interceptor);
         }  
      }
   }   

   @Override
   public void onDispatch(Session session, Frame frame) { 
      if(enable) {
         DeltaInterceptor interceptor = interceptors.fetch(session);
         
         if(interceptor != null) {
            DataReader reader = frame.getReader();
            FrameType type = frame.getType();
            
            if(type == MESSAGE) {
               try {                  
                  interceptor.consume(reader);
               } catch(Exception e) {
                  LOG.info("Could not dispatch frame from " + session, e);
               }
            }
         }
      }
   }
 

   @Override
   public void onException(Session session, Exception cause) {
      if(enable) {
         DeltaInterceptor interceptor = interceptors.take(session);
         
         if(interceptor != null) {
            interceptor.clear();
         }
      }
   }

   @Override
   public void onClose(Session session) {
      if(enable) {
         DeltaInterceptor interceptor = interceptors.take(session);
         
         if(interceptor != null) {
            interceptor.clear();
         }
      }
   }
}
