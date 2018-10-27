package com.authrus.gateway.test;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

public class JCEStrengthTest {

   public static void main(String[] list) throws Exception {
      try {
         int maxAllowedKeyLength = Cipher.getMaxAllowedKeyLength("AES");
         System.out.println("AES: " + maxAllowedKeyLength);
         System.out.println(maxAllowedKeyLength >= 256);
     } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
     }
   }
}
