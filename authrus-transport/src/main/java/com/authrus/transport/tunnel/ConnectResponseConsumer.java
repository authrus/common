package com.authrus.transport.tunnel;

import java.io.IOException;

import org.simpleframework.common.buffer.ArrayBuffer;
import org.simpleframework.common.buffer.Buffer;
import org.simpleframework.transport.ByteCursor;

class ConnectResponseConsumer implements ConnectResponse {

   private static final byte[] TERMINAL = {'\r', '\n', '\r', '\n'};

   private TunnelState state;
   private String header;
   private String status;
   private Buffer buffer;     
   private byte[] block;
   private int count;

   public ConnectResponseConsumer() {
      this.buffer = new ArrayBuffer(1024, 32768);      
      this.block = new byte[8192];
   }
   
   public String getStatus() {
      return status;   
   }
   
   public String getHeader() {
      return header;
   }   
   
   public TunnelState getState() {   
      return state;
   }   

   public void consume(ByteCursor cursor) throws IOException {
      while (cursor.isReady()) {        
         int size = cursor.read(block, 0, block.length);  
         int seek = 0;
            
         while(seek < size) {
            byte next = block[seek++];
            
            if(count >= TERMINAL.length){
               break;
            }
            if(next != TERMINAL[count++]) {
               count = 0;
            }                           
         }
         buffer.append(block, 0, size);  
         
         if(count >= TERMINAL.length) {
            String message = buffer.encode();
            int length = message.length();
            
            if(length > 12) {
               status = message.substring(9, 12);
               state = TunnelState.resolveState(status);
            }
            header = message;
         }
      }
   }

   public boolean isFinished() {
      return header != null;
   }
}
