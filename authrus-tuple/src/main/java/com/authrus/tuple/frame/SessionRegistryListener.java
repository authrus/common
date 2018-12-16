package com.authrus.tuple.frame;

import org.simpleframework.transport.Channel;

public class SessionRegistryListener extends FrameAdapter {

   private final SessionRegistry registry;
   
   public SessionRegistryListener(SessionRegistry registry) {
      this.registry = registry;
   }   

   @Override
   public void onConnect(Session session, Channel channel) {
      registry.remove(session);
   }   

   @Override
   public void onDispatch(Session session, Frame frame) {
      int size = frame.getSize();
      
      if(!registry.contains(session)) {
         registry.register(session);
      }
      registry.update(session, size);
   }
   
   @Override
   public void onSuccess(Session session, int sequence) {
      registry.receipt(session);
   }

   @Override
   public void onException(Session session, Exception cause) {
      registry.remove(session);
   }
   
   @Override
   public void onClose(Session session) {
      registry.remove(session);
   }
}
