package com.authrus.sso.jwt;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenConfiguration {
   
   private final TokenKeyProvider provider;

   public TokenConfiguration(
         @Value("${login.token-store.type:PKCS}") TokenStoreType type,
         @Value("${login.token-store.path}") File path,
         @Value("${login.token-store.alias}") String alias,
         @Value("${login.token-store.password}") String password)
   {
      this.provider = new TokenKeyProvider(type, path, alias, password);
   }
   
   @Bean
   public TokenGenerator tokenGenerator() {
      return new TokenGenerator(provider);
   }
   
   @Bean
   public TokenParser tokenParser(TokenGenerator generator) {
      return new TokenParser(generator);
   }
}
