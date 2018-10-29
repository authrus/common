package com.authrus.gateway.deploy.build;

import lombok.Data;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@ToString
class RouteRule {
   
   private String pattern;
   private String template;
   
   @JsonCreator
   public RouteRule(
         @JsonProperty("pattern") String pattern,
         @JsonProperty("template") String template)
   {
      this.pattern = pattern;
      this.template = template;
   }
}