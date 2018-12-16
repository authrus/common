package com.authrus.transport.tunnel;

import static com.authrus.transport.tunnel.TunnelState.ERROR;
import static com.authrus.transport.tunnel.TunnelState.REJECTED;

import java.util.concurrent.BlockingQueue;

import org.simpleframework.transport.Channel;

class TunnelStatusUpdater implements TunnelListener {

   private final BlockingQueue<Tunnel> queue;

   public TunnelStatusUpdater(BlockingQueue<Tunnel> queue) {
      this.queue = queue;      
   }   

   @Override
   public void onResponse(Channel channel, TunnelState state) {
      Tunnel tunnel = new Tunnel(state, channel);     
      
      if(queue != null) {
         queue.offer(tunnel);  
      }
   }

   @Override
   public void onFailure(Channel channel, Exception cause) {
      Tunnel tunnel = new Tunnel(ERROR, channel, cause);
      
      if(queue != null) {
         queue.offer(tunnel);
      }
   }

   @Override
   public void onReject(Channel channel) {
      Tunnel tunnel = new Tunnel(REJECTED, channel);
      
      if(queue != null) {
         queue.offer(tunnel);
      }
   }
}
