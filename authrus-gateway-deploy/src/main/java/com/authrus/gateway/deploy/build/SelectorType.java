package com.authrus.gateway.deploy.build;

public enum SelectorType {
   MASTER_SLAVE("master-slave"),
   DYNAMIC("dynamic");
   
   private final String type;
   
   private SelectorType(String type) {
      this.type = type;
   }
   
   public static SelectorType resolve(String token) {
      if(token != null) {
         SelectorType[] types = SelectorType.values();
         
         for(SelectorType type : types) {
            if(type.type.equalsIgnoreCase(token)) {
               return type;
            }
         }
         throw new IllegalArgumentException("No such selector " + token);
      }
      return DYNAMIC;
   }
   
}
