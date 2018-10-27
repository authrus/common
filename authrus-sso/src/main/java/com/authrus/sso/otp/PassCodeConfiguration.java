package com.authrus.sso.otp;

import com.authrus.store.DataStore;
import com.authrus.store.DataStoreBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = PassCodeConfiguration.class)
public class PassCodeConfiguration {

   private final int high;
   private final int low;

   public PassCodeConfiguration(
         @Value("${login.pass-code.high}") int high,
         @Value("${login.pass-code.low}") int low)
   {
      this.high = high;
      this.low = low;
   }
   
   @Bean
   public PassCodeRepository passCodeRepository(DataStoreBuilder builder) {
      DataStore<PassCode> store = builder.create(PassCode.class);
      return new PassCodeRepository(store);
   }
   
   @Bean
   public PassCodeCalculator passCodeCalculator(PassCodeRepository repository) {
      return new PassCodeCalculator(repository, high, low);
   }
}
