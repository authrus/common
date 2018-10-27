package com.authrus.directory.user;

import com.authrus.domain.user.User;
import com.authrus.store.DataStore;
import com.authrus.store.DataStoreBuilder;
import com.authrus.store.DataStoreConfiguration;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AllArgsConstructor
@Import(DataStoreConfiguration.class)
public class UserConfiguration {
   
   private final DataStoreBuilder builder;

   @Bean
   public UserDirectory userRepository() {
      DataStore<User> store = builder.create(User.class);
      return new UserDirectory(store);
   }
}
