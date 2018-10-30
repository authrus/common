package com.authrus.gateway.deploy.build;

import java.util.Map;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class PropertySet {
   
   private final Map<String, String> properties;
   
   @JsonCreator
   public PropertySet(
         @JsonProperty(value = "properties", required = false) Map<String, String> properties)
   {
      this.properties = properties;
   }
}