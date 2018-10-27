package com.authrus.sso.access;

import com.authrus.store.DataStore;
import com.authrus.store.DataStoreBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = AccessConfiguration.class)
public class AccessConfiguration {

   @Bean      
   public AccessRequestRepository accessRequestRepository(DataStoreBuilder builder) {
      DataStore<AccessRequest> store = builder.create(AccessRequest.class);
      return new AccessRequestRepository(store);
   }
   
   @Bean      
   public AccessGrantRepository accessGrantRepository(DataStoreBuilder builder) {
      DataStore<AccessGrant> store = builder.create(AccessGrant.class);
      return new AccessGrantRepository(store);
   }
}
