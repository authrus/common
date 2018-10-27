package com.authrus.rest.manage;

import com.authrus.rest.registry.RegistryNode;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagementData {

   private Map<String, String> attributes;
   private RegistryNode node;
   private String connector; // JMX connector
   private String name;
   private String address;
}
