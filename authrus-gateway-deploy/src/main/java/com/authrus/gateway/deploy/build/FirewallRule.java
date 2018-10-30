package com.authrus.gateway.deploy.build;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FirewallRule {

   private final String type;
   private final String host;
   private final String address;
   private final int port;
}
