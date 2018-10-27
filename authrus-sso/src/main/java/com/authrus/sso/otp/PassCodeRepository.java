package com.authrus.sso.otp;

import com.authrus.store.DataStore;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PassCodeRepository {

   private final DataStore<PassCode> store;
   
   public void save(PassCode code) {
      store.save(code);
   }
   
   public void deleteByCode(int code){
      store.delete("code", code);
   }
   
   public PassCode findByCode(int code){
      return store.find("code", code);
   }
  
   public Iterable<PassCode> findAll() {
      return store.findAll();
   }
}
