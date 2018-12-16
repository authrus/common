package com.authrus.transport.tunnel;

import java.io.InputStream;
import java.net.Socket;

import junit.framework.TestCase;

public class ConnectTest extends TestCase{
   public void testConnect()throws Exception{
      String h = "CONNECT www.google.com:9090 HTTP/1.1\r\n"+
                 "Host: www.google.com:9090\r\n"+
                 "\r\n";
      byte[] r =h.getBytes();
      Socket socket = new Socket("localhost", 80);
      socket.getOutputStream().write(r);
      socket.getOutputStream().flush();
      InputStream in = socket.getInputStream();
      int count = 0;
      while((count=in.read())!=-1){
         System.err.write(count);
         System.err.flush();
      }
            
   }

}
