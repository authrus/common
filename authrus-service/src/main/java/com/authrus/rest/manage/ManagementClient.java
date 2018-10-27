package com.authrus.rest.manage;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import com.authrus.rest.registry.RegistryNode;
import com.authrus.rest.registry.RegistryService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@AllArgsConstructor
public class ManagementClient {

   private final RegistryService service;
   
   public List<ManagementData> getData() {
      List<RegistryNode> nodes = service.getNodes();
      return nodes.parallelStream()
            .map(node -> {
               try {
                  String address = node.getAddress();
                  ManagementData data = WebClient.create(address)                        
                        .get()
                        .uri("/v1/manage")
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(ManagementData.class)
                        .block();
             
                  data.setNode(node);
                  return data;
               } catch(Exception e) {
                  log.info("Could not retrieve data", e);
               }
               return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
      
   }
}
