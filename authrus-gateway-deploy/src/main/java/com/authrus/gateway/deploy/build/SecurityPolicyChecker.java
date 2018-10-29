package com.authrus.gateway.deploy.build;

import javax.crypto.Cipher;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityPolicyChecker {

   @SneakyThrows
   public static void checkPolicy(){
      int maximum = Cipher.getMaxAllowedKeyLength("AES");
      
      log.info("AES maximum allowed key length: " + maximum);
      
      if(maximum < 256) {
         throw new IllegalStateException("JCE unlimited strength jurisdiction policy files not installed");
      }
   }
}
