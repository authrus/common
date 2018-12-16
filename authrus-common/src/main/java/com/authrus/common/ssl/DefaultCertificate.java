package com.authrus.common.ssl;

import javax.net.ssl.SSLContext;

public class DefaultCertificate implements Certificate {
   
   private final String[] protocols;
   private final String[] suites;
   
   public DefaultCertificate() {
      this(null);
   }
   
   public DefaultCertificate(String[] protocols) {
      this(protocols, null);
   }
   
   public DefaultCertificate(String[] protocols, String[] suites) {
      this.protocols = protocols;
      this.suites = suites;
   }

   @Override
   public SSLContext getContext() { 
      try {
         return SSLContext.getDefault();
      } catch(Exception e) {
         throw new CertificateException("Could not create default context", e);     
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
