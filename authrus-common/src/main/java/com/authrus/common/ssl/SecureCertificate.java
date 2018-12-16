package com.authrus.common.ssl;

import javax.net.ssl.SSLContext;

public class SecureCertificate implements Certificate {
   
   private final SecureSocketContext context;
   private final String[] protocols;
   private final String[] suites;

   public SecureCertificate(SecureSocketContext context) throws Exception {
      this(context, null);
   }
   
   public SecureCertificate(SecureSocketContext context, String[] protocols) throws Exception {
      this(context, protocols, null);
   }
   
   public SecureCertificate(SecureSocketContext context, String[] protocols, String[] suites) throws Exception {
      this.protocols = protocols;
      this.context = context;
      this.suites = suites;
   }
   
   @Override
   public SSLContext getContext() {
      try {
         return context.getContext();
      } catch(Exception e) {
         throw new CertificateException("Could not create context", e);         
      }
   } 
   
   @Override
   public String[] getCipherSuites() {
      return suites;
   }
   
   @Override
   public String[] getProtocols() {
      return protocols;
   }   
}
