package com.authrus.sso.otp;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class PassCodeCalculator {

   private final PassCodeRepository repository;
   private final SecureRandom random;
   private final int high;
   private final int low;
   
   public PassCodeCalculator(PassCodeRepository repository, int high, int low) {
      this.random = new SecureRandom();
      this.repository = repository;
      this.high = high;
      this.low = low;
   }
   
   public PassCode calculate(String email) {
      long time = System.currentTimeMillis();
      
      for(int i = 0; i < 1000; i++) {
         int code = random.nextInt(high - low) + low;
         PassCode existing = repository.findByCode(code);
         long expiry = existing == null ? 0 : existing.getExpiry();
         
         if(expiry < time) {
            long duration = TimeUnit.DAYS.toMillis(10);
            PassCode result = PassCode.builder()
                  .code(code)
                  .email(email)
                  .expiry(duration + time)
                  .build();
            
            repository.save(result);
            return result;
         }
      }
      throw new IllegalStateException("Could not calculate code for " + email);
   }
}
