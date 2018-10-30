package com.authrus.gateway.deploy.build;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Status;
import org.simpleframework.transport.trace.TraceAnalyzer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zuooh.common.ssl.Certificate;
import com.zuooh.http.proxy.ProxyContainer;
import com.zuooh.http.proxy.host.DomainResolver;
import com.zuooh.http.proxy.host.HostResolver;
import com.zuooh.http.proxy.resource.host.ResourceHost;
import com.zuooh.http.resource.StringResource;

@Slf4j
public class Specification {
   
   private final Map<String, String> properties;
   private final List<HostLayout> hosts;
   
   @JsonCreator
   public Specification(
         @JsonProperty("properties") Map<String, String> properties,
         @JsonProperty("hosts") List<HostLayout> hosts)         
   {
      this.properties = properties;
      this.hosts = hosts;
   }
   
   @SneakyThrows
   public void process(Context context){  
      Executor executor = context.getExecutor();
      TraceAnalyzer analyzer = context.getAgent();
      Map<String, HostResolver> domains = new LinkedHashMap<String, HostResolver>();
      StringResource resource = new StringResource("No host matched", "text/plain", "UTF-8", Status.OK);
      ResourceHost error = new ResourceHost(Collections.EMPTY_MAP, resource);
      DomainResolver domainResolver = new DomainResolver(domains, error);
      ProxyContainer container = new ProxyContainer(domainResolver, executor);
      ProxyServer processor = new ProxyServer(container, analyzer, 40);
      Set<Integer> ports = new HashSet<Integer>();
      List<Destination> details = new ArrayList<Destination>();
      
      for(HostLayout host : hosts) {
         HostResolver resolver = host.createResolver(context);
         InetSocketAddress hostAddress = new InetSocketAddress(host.getPort());
         KeyStore store = host.getStore();
         String name = host.getName();
         int port = host.getPort();
         
         if(!ports.add(host.getPort())) {
            throw new IllegalArgumentException("Port " + port + " already used");
         }
         domains.put(name + ":" + port, resolver);
         domains.put(name, resolver);
         
         if(store != null) {   
            Certificate certificate = store.getCertificate();
            Destination virtualDetails = new Destination(hostAddress, certificate, name);
            details.add(virtualDetails);
            context.getAddresses().add(URI.create("https://" + name));
         } else {
            Destination virtualDetails = new Destination(hostAddress, null, name);
            details.add(virtualDetails);
         }
         log.info("Domain {} is listening on {}", name, port);
         context.getAddresses().add(URI.create("http://" + name)); // HTTP will always be supported
      }
      for(Destination host : details) {
         Certificate certificate = host.getCertificate();
         InetSocketAddress address = host.getAddress();
         
         if(certificate != null) {
            processor.start(address, certificate);
         }else {
            processor.start(address);
         }
      }
   }
}