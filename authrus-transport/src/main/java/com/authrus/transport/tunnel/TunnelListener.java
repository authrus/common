package com.authrus.transport.tunnel;

import org.simpleframework.transport.Channel;

interface TunnelListener {
   void onResponse(Channel channel, TunnelState state);
   void onFailure(Channel channel, Exception cause);
   void onReject(Channel channel);
}
