package com.authrus.common.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class AnonymousTrustManager implements X509TrustManager {

   public boolean isClientTrusted(X509Certificate[] certificate) {
      return true;
   }

   public boolean isServerTrusted(X509Certificate[] certificate) {
      return true;
   }

   public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
   }

   public void checkClientTrusted(X509Certificate[] certificate, String authentication) throws CertificateException {}   

   public void checkServerTrusted(X509Certificate[] certificate, String authentication) throws CertificateException {}
}
