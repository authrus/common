package com.authrus.common.ssl;

public class CertificateException extends SecurityException {

   public CertificateException(String message) {
      super(message);
   }
   
   public CertificateException(String message, Throwable cause) {
      super(message, cause);
   }
}
