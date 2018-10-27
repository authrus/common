package com.authrus.sso.jwt;

import java.security.interfaces.RSAKey;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.Sets;

@AllArgsConstructor
public class TokenGenerator {

   private final TokenKeyProvider provider; 

   public Token generateToken(Map<String, String> claims, long expiry) {
      RSAKey key = provider.getPrivateKey();
      Algorithm algorithm = Algorithm.RSA256(key);
      JWTCreator.Builder builder = JWT.create();
      Set<String> names = claims.keySet();
      Set<String> sorted = Sets.newTreeSet(names); // must be sorted
      
      for(String name : sorted) {
         String claim = claims.get(name);
         builder.withClaim(name, claim);
      }
      String token = builder.sign(algorithm);
      return new Token(claims, token, expiry);
   }
}
