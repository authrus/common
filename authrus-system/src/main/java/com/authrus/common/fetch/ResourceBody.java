package com.authrus.common.fetch;

import java.io.InputStream;

public class ResourceBody {

   private final InputStream source;
   private final int status;
   
   public ResourceBody(InputStream source, int status) {
      this.source = source;
      this.status = status;
   }
   
   public InputStream getSource() {
      return source;
   }
   
   public int getStatus() {
      return status;
   }
   
}
