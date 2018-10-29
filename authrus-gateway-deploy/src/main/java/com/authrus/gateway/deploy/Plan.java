package com.authrus.gateway.deploy;

import java.net.URI;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Plan {

   private final Set<FirewallRule> rules;
   private final Set<EndPoint> servers;
   private final Set<URI> addresses;
}
