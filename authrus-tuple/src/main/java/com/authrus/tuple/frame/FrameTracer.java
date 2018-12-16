package com.authrus.tuple.frame;

import org.simpleframework.transport.Channel;

public interface FrameTracer {
   void onConnect(Session session, Channel channel);
   void onDispatch(Session session, Frame frame);
   void onException(Session session, Exception cause);
   void onSuccess(Session session, int sequence);
   void onFailure(Session session, int sequence);
   void onHeartbeat(Session session);
   void onClose(Session session);
}
