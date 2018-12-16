package com.authrus.common.security;

import java.security.Principal;

public class UserPrincipal implements Principal {
   
   private final String name;
   
   public UserPrincipal(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public boolean equals(Object other) {
      if(other instanceof UserPrincipal) {
         return equals((UserPrincipal)other);
      }
      return false;
   }
   
   public boolean equals(UserPrincipal other) {
      if(other != null) {
         return other.name.equals(name);
      }
      return false;
   }
   
   @Override
   public int hashCode() {
      return name.hashCode();
   }   
   
   @Override
   public String toString(){
      return name;
   }

}
