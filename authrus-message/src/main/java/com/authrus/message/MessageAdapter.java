package com.authrus.message;

public class MessageAdapter implements MessageListener {
   public void onMessage(Message message) {}
   public void onException(Exception cause) {}
   public void onHeartbeat() {}
   public void onReset() {}
}
