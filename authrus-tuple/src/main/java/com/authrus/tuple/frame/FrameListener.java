package com.authrus.tuple.frame;

/**
 * The frame listener listens to frames sent over a connection. It also listens
 * to various lifecycle events on the connection such as connection closure or
 * the creation of a new connection.
 * 
 * @author Niall Gallagher
 */
public interface FrameListener {
   void onConnect();
   void onFrame(Frame frame);
   void onException(Exception cause);
   void onSuccess(Sequence sequence);
   void onHeartbeat();
   void onClose();
}
