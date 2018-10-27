package com.authrus.directory.user;

import com.authrus.domain.user.User;
import com.authrus.store.DataStore;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDirectory {

   private final DataStore<User> store;
   
   public void save(User user) {
      store.save(user);
   }
   
   public User findByEmail(String email) {
      return store.find("email", email);
   }
   
   public User findByGuid(String guid) {
      return store.find("guid", guid);
   }
}
