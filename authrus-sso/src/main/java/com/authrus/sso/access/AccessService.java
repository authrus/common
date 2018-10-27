package com.authrus.sso.access;

import com.authrus.sso.jwt.Token;
import com.authrus.sso.jwt.TokenGenerator;
import com.authrus.sso.jwt.TokenParser;
import com.authrus.sso.otp.PassCode;
import com.authrus.sso.otp.PassCodeCalculator;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccessService {

   private final AccessRequestRepository requests;
   private final AccessGrantRepository grants;
   private final PassCodeCalculator calculator;
   private final TokenGenerator generator;
   private final TokenParser parser;
  
   public List<AccessGrant> listGrants() {
      return grants.findAll();
   }
   
   public List<AccessRequest> listRequests() {
      return requests.findAll();
   }
   
   public AccessRequest createRequest(String email, String redirect) {
      String token = UUID.randomUUID().toString();
      PassCode code = calculator.calculate(email);
      long time = System.currentTimeMillis();
      int value = code.getCode();
      AccessRequest request = AccessRequest.builder()
            .token(token)
            .code(value)
            .email(email)
            .redirect(redirect)
            .expiry(time + TimeUnit.MINUTES.toMillis(20))
            .lease(time + TimeUnit.DAYS.toMillis(1))
            .build();
      
      requests.save(request);
      return request;
   }
   
   public AccessGrant grantAccess(String token) {
      AccessRequest request = requests.findByToken(token);
      long time = System.currentTimeMillis();
      
      if(request != null) {
         long expiry = request.getExpiry();
         
         if(expiry > time) {
            Map<String, String> claims = request.getClaims();
            long lease = request.getLease();
            Token value = generator.generateToken(claims, lease);
            String cookie = value.getToken();
            String redirect = request.getRedirect();
            String email = request.getEmail();
            AccessGrant grant = AccessGrant.builder()
                  .cookie(cookie)
                  .email(email)
                  .redirect(redirect)
                  .expiry(lease)
                  .build();
            
            grants.save(grant);
            return grant;
         }
         requests.deleteByToken(token);
      }
      return null;
   }
   
   public AccessGrant grantAccess(int code) {
      AccessRequest request = requests.findByCode(code);
      long time = System.currentTimeMillis();
      
      if(request != null) {
         long expiry = request.getExpiry();
         
         if(expiry > time) {
            Map<String, String> claims = request.getClaims();
            long lease = request.getLease();
            Token value = generator.generateToken(claims, lease);
            String cookie = value.getToken();
            String redirect = request.getRedirect();
            String email = request.getEmail();
            AccessGrant grant = AccessGrant.builder()
                  .cookie(cookie)
                  .email(email)
                  .redirect(redirect)
                  .expiry(lease)
                  .build();
            
            grants.save(grant);
            return grant;
         }
         requests.deleteByCode(code);
      }
      return null;
   }
   
   public boolean verifyAccess(String cookie) {
      AccessGrant grant = grants.findByCookie(cookie);
      long time = System.currentTimeMillis();
      
      if(grant != null) {
         long expiry = grant.getExpiry();
         
         if(expiry > time) {
            return true;
         }
         grants.deleteByCookie(cookie);
      } else {
         Token token = parser.parseToken(cookie);
         
         if(token != null) {
            long expiry = token.getExpiry();
            
            if(expiry > time) {
               return true;
            }
         }
      }
      return false;
   }
}
