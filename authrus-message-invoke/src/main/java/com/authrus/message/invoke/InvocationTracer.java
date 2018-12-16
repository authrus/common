package com.authrus.message.invoke;

import com.authrus.tuple.subscribe.SubscriptionListener;

public interface InvocationTracer extends SubscriptionListener {
   void onInvoke(String operation, Invocation invocation);
   void onReturn(String operation);
}
