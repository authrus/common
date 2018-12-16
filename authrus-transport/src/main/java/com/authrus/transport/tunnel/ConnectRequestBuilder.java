package com.authrus.transport.tunnel;

class ConnectRequestBuilder {
   
   private final String host; 
   private final String path;
   
   public ConnectRequestBuilder(String host, String path){
      this.host = host;  
      this.path = path;
   }
   
   public String createRequest() {
      StringBuilder builder = new StringBuilder();
      
      builder.append("CONNECT ");
      builder.append(host);
      builder.append(path);
      builder.append(" HTTP/1.1\r\n");
      builder.append("Host: ");
      builder.append(host);
      builder.append("\r\n");
      builder.append("\r\n");
      
      return builder.toString();
   }

}
