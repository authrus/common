package com.authrus.sso.access;

import com.authrus.store.DataStore;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccessRequestRepository {
   
   private final DataStore<AccessRequest> store;
   
   public void save(AccessRequest request) {
      store.save(request);
   }
   
   public void deleteByToken(String token){
      store.delete("token", token);
   }
   
   public void deleteByCode(int code){
      store.delete("code", code);
   }
   
   public AccessRequest findByToken(String token){
      return store.find("token", token);
   }
   
   public AccessRequest findByCode(int code){
      return store.find("code", code);
   }
  
   public List<AccessRequest> findAll() {
      return store.findAll();
   }
}
