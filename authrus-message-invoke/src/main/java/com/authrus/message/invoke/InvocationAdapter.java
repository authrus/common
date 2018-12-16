package com.authrus.message.invoke;

import com.authrus.tuple.subscribe.SubscriptionAdapter;

public class InvocationAdapter extends SubscriptionAdapter implements InvocationTracer {
   public void onInvoke(String operation, Invocation invocation) {}
   public void onReturn(String operation) {}
   public void onException(Exception cause) {}
}
