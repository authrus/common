package com.authrus.gateway.deploy.build;

import java.io.File;
import java.util.Arrays;

import javax.crypto.Cipher;

import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zuooh.common.ssl.Certificate;
import com.zuooh.common.ssl.DefaultCertificate;
import com.zuooh.common.ssl.KeyStoreReader;
import com.zuooh.common.ssl.KeyStoreType;
import com.zuooh.common.ssl.SecureCertificate;
import com.zuooh.common.ssl.SecureProtocol;
import com.zuooh.common.ssl.SecureSocketContext;

@Slf4j
@ToString 
class KeyStore {
   
   private final KeyStoreType type;
   private final String path;
   private final String password;
   private final String[] protocols;
   private final String[] suites;
   
   @JsonCreator
   public KeyStore(
         @JsonProperty("type") KeyStoreType type,
         @JsonProperty("path") String path,
         @JsonProperty("password") String password,
         @JsonProperty("protocols") String[] protocols,
         @JsonProperty("cipher-suites") String[] suites)
   {
      this.password = password;
      this.protocols = protocols;
      this.suites = suites;
      this.path = path;
      this.type = type;
   }

   @SneakyThrows
   public Certificate getCertificate() {
      SecurityPolicyChecker.checkPolicy();
      
      if(path == null && password != null) {
         throw new IllegalStateException("Key store path not specified");
      }
      if(path != null) {
         File file = new File(path);
         
         if(!file.exists()) {
            throw new IllegalStateException("Key store file " + file + " not found");
         }
         if(file.isDirectory()) {
            throw new IllegalStateException("Key store file " + file + " must a file of type " + type + " and not a directory");
         }
         KeyStoreReader reader = new KeyStoreReader(type, file, password, password);
         SecureSocketContext context = new SecureSocketContext(reader, SecureProtocol.TLS);
         
         log.info("Creating certificate from {} with protocols [{}] and cipher suites [{}]", file, Arrays.toString(protocols), Arrays.toString(suites));
   
         return new SecureCertificate(context, protocols, suites);                  
      }
      return new DefaultCertificate(protocols, suites);
   }
}