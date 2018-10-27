package com.authrus.sso.jwt;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAKey;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;

@AllArgsConstructor
public class TokenKeyProvider {

   private final TokenStoreType type;
   private final File path;
   private final String alias;
   private final String password;
   
   @SneakyThrows
   public RSAKey getPrivateKey() {
      char[] data = password.toCharArray();
      KeyStore store = type.getKeyStore();
      InputStream stream = FileUtils.openInputStream(path);
      
      store.load(stream, data);
   
      return (RSAKey)store.getKey(alias, data);
   }
}
