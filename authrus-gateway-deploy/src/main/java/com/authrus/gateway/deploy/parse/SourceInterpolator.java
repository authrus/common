package com.authrus.gateway.deploy.parse;

import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;

import com.authrus.gateway.deploy.build.Context;
import com.authrus.gateway.deploy.build.PropertySet;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SourceInterpolator {

   @SneakyThrows
   public static String interpolate(Context context, String source) {
      ObjectMapper mapper = context.getMapper();
      PropertySet properties = mapper.readValue(source, PropertySet.class);
      Map<String, String> attributes = properties.getProperties();
      
      if(attributes != null) {
         Set<Map.Entry<String, String>> entries = attributes.entrySet();
         
         for(Map.Entry<String, String> entry : entries){
            String key = entry.getKey();
            String value = entry.getValue();
            
            source = source.replace("${" + key + "}", value);
         }
      }
      return source;
   }
}
