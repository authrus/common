package com.authrus.attribute.transform;

import java.net.URL;

public class URLTransform implements ObjectTransform<URL, String> {

   public URL toObject(String target) throws Exception {
      return new URL(target);
   }

   public String fromObject(URL target) throws Exception {
      return target.toString();
   }
}
