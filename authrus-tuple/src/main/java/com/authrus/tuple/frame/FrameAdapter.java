package com.authrus.tuple.frame;

import org.simpleframework.transport.Channel;

public class FrameAdapter implements FrameTracer {
   public void onConnect(Session session, Channel channel) {}
   public void onDispatch(Session session, Frame frame) {}
   public void onException(Session session, Exception cause) {}
   public void onSuccess(Session session, int sequence) {}
   public void onFailure(Session session, int sequence) {}
   public void onHeartbeat(Session session) {}
   public void onClose(Session session) {}
}
