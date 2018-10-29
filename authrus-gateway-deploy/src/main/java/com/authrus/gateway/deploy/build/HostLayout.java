package com.authrus.gateway.deploy.build;

import static com.zuooh.http.proxy.core.State.CERTIFICATE_EXPIRED;
import static com.zuooh.http.proxy.core.State.CERTIFICATE_INVALID;
import static com.zuooh.http.proxy.core.State.CERTIFICATE_REQUIRED;
import static com.zuooh.http.proxy.core.State.CERTIFICATE_REVOKED;
import static com.zuooh.http.proxy.core.State.CERTIFICATE_UNTRUSTED;
import static com.zuooh.http.proxy.core.State.COOKIE_EXPIRED;
import static com.zuooh.http.proxy.core.State.COOKIE_SPOOF;
import static com.zuooh.http.proxy.core.State.PERMISSION_DENIED;
import static com.zuooh.http.proxy.core.State.REMOTE_DISCONNECT;
import static com.zuooh.http.proxy.core.State.REMOTE_TIMEOUT;
import static com.zuooh.http.proxy.core.State.RESTRICTED_METHOD;
import static com.zuooh.http.proxy.core.State.UNABLE_TO_CONNECT;
import static com.zuooh.http.proxy.core.State.UNEXPECTED_ERROR;
import static org.simpleframework.http.Status.BAD_GATEWAY;
import static org.simpleframework.http.Status.FORBIDDEN;
import static org.simpleframework.http.Status.GATEWAY_TIMEOUT;
import static org.simpleframework.http.Status.INTERNAL_SERVER_ERROR;
import static org.simpleframework.http.Status.METHOD_NOT_ALLOWED;
import static org.simpleframework.http.Status.SERVICE_UNAVAILABLE;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

import org.simpleframework.http.Scheme;
import org.simpleframework.http.Status;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.gateway.deploy.Context;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zuooh.common.ssl.Certificate;
import com.zuooh.http.proxy.balancer.LoadBalancer;
import com.zuooh.http.proxy.balancer.LoadBalancerHost;
import com.zuooh.http.proxy.core.State;
import com.zuooh.http.proxy.core.exchange.ReactorController;
import com.zuooh.http.proxy.core.intercept.CombinationInterceptor;
import com.zuooh.http.proxy.core.intercept.CopyHeaderInterceptor;
import com.zuooh.http.proxy.core.intercept.HeaderInterceptor;
import com.zuooh.http.proxy.core.intercept.ResponseInterceptor;
import com.zuooh.http.proxy.host.Host;
import com.zuooh.http.proxy.host.HostResolver;
import com.zuooh.http.proxy.host.RegularExpressionResolver;
import com.zuooh.http.proxy.host.SecureResolver;
import com.zuooh.http.proxy.log.FileLog;
import com.zuooh.http.proxy.resource.ResourceReporter;
import com.zuooh.http.proxy.resource.host.ResourceHost;
import com.zuooh.http.proxy.resource.redirect.Redirect;
import com.zuooh.http.proxy.resource.redirect.RedirectResolver;
import com.zuooh.http.proxy.resource.redirect.RegularExpressionRedirector;
import com.zuooh.http.proxy.resource.redirect.ResourceRedirector;
import com.zuooh.http.proxy.security.DirectAccessManager;
import com.zuooh.http.resource.FileManager;
import com.zuooh.http.resource.FileResource;
import com.zuooh.http.resource.Resource;
import com.zuooh.http.resource.StringResource;

@Data
@ToString 
class HostLayout {

   private final List<RedirectRule> redirects;
   private final List<Route> routes;
   private final String directory;
   private final KeyStore store;
   private final AccessLog log;
   private final String name;
   private final int port;
   
   @JsonCreator
   public HostLayout(
         @JsonProperty("redirects") List<RedirectRule> redirects,
         @JsonProperty("routes") List<Route> routes,
         @JsonProperty("directory") String directory,
         @JsonProperty("key-store") KeyStore store,
         @JsonProperty("access-log") AccessLog log,
         @JsonProperty("name") String name,
         @JsonProperty("port") int port)
   {
      this.redirects = redirects;
      this.directory = directory;
      this.routes = routes;
      this.store = store;
      this.name = name;
      this.port = port;
      this.log = log;
   }

   public Certificate createCertificate() {
      if(store != null) {
         return store.getCertificate();
      }
      return null;
   }
   
   @SneakyThrows
   public HostResolver createResolver(Context context){
      if(name == null) {
         throw new IllegalStateException("Host must have a name");
      }
      if(directory == null) {
         throw new IllegalStateException("Host must specify a static resources directory");
      }
      Map<State, Resource> resources = createResources();
      Reactor reactor = context.getReactor();
      FileLog fileLog = log.createLog(directory);
      Host invalidHost = createInvalidHost(context);
      ReactorController controller = new ReactorController(reactor);
      DirectAccessManager manager = new DirectAccessManager(State.ACCESS_GRANTED);
      ResourceReporter reporter = new ResourceReporter(resources, fileLog, name);
      Map<String, Host> hosts = new LinkedHashMap<String, Host>();
      HostResolver resolver = new RegularExpressionResolver(hosts, invalidHost);
      List<ResponseInterceptor> interceptors = new ArrayList<ResponseInterceptor>();
      CombinationInterceptor interceptor = new CombinationInterceptor(interceptors);
      String hostname = InetAddress.getLocalHost().getCanonicalHostName();
      
      interceptors.add(new CopyHeaderInterceptor());
      interceptors.add(new HeaderInterceptor("X-Proxy-Node", "proxy@" + hostname));
      
      for(Route route : routes) {
         LoadBalancer balancer = route.createBalancer(context, name);
         LoadBalancerHost host = new LoadBalancerHost(balancer, manager, controller, reporter, interceptor);
         
         for(String match : route.getPatterns()) {
            hosts.put(match, host);
         }
      }
      fileLog.start();
      
      if(redirects != null && !redirects.isEmpty()) {
         return createRedirects(context, resolver);
      }
      return resolver;
   }
   
   public Host createInvalidHost(Context context) {
      Resource statusResource = new StringResource("Everything is ok...", "text/plain", "UTF-8", Status.OK);
      Resource errorResource = new StringResource("Could not find resource...", "text/plain", "UTF-8", Status.NOT_FOUND);
      Map<String, Resource> resources = new LinkedHashMap<String, Resource>();
      ResourceHost host = new ResourceHost(resources, errorResource);
      resources.put("/.*", statusResource);
      return host;
   }
   
   public HostResolver createRedirects(Context context, HostResolver resolver) {
      Map<String, Redirect> secureRedirects = new LinkedHashMap<>();
      Map<String, Redirect> normalRedirects = new LinkedHashMap<>();
      Resource redirectResource = new StringResource("Redirecting ...", "text/plain", "UTF-8", Status.FOUND);
      Resource errorResource = new StringResource("Error ...", "text/plain", "UTF-8", Status.NOT_FOUND);
      ResourceRedirector secureRedirector = new RegularExpressionRedirector(secureRedirects, redirectResource);
      ResourceRedirector normalRedirector = new RegularExpressionRedirector(normalRedirects, redirectResource); 
      RedirectResolver secureResolver = new RedirectResolver(resolver, secureRedirector, errorResource);
      RedirectResolver normalResolver = new RedirectResolver(resolver, normalRedirector, errorResource);
      
      for(RedirectRule rule :redirects){
         try {
            RedirectEntry entry = rule.createRedirect();
            Redirect redirect = entry.getRedirect();
            Scheme scheme = entry.getScheme();
            String path = entry.getPath();
            
            if(scheme == Scheme.HTTP) {
               normalRedirects.put(path, redirect);
            } else {
               secureRedirects.put(path, redirect);
            }
         }catch(Exception e) {
            throw new IllegalStateException("Could not parse redirect expression", e);
         }
      }
      return new SecureResolver(normalResolver, secureResolver);
   }

   
   public Map<State, Resource> createResources() {
      File files = new File(directory);
      Map<State, Resource> resources = new LinkedHashMap<>();
      
      if(!files.exists()) {
         throw new IllegalStateException("Resource directory " + files + " is empty");
      }
      FileManager manager = new FileManager(files);
      resources.put(REMOTE_DISCONNECT, new FileResource(manager, new File(files, "error/remote-disconnect.html"), "text/html", BAD_GATEWAY));
      resources.put(REMOTE_TIMEOUT, new FileResource(manager, new File(files, "error/remote-timeout.html"), "text/html", GATEWAY_TIMEOUT));
      resources.put(UNEXPECTED_ERROR, new FileResource(manager, new File(files, "error/internal-error.html"), "text/html", INTERNAL_SERVER_ERROR));
      resources.put(UNABLE_TO_CONNECT, new FileResource(manager, new File(files, "error/service-unavailable.html"), "text/html", SERVICE_UNAVAILABLE));
      resources.put(COOKIE_SPOOF, new FileResource(manager, new File(files, "error/cookie-spoof.html"), "text/html", FORBIDDEN));
      resources.put(COOKIE_EXPIRED, new FileResource(manager, new File(files, "error/cookie-expired.html"), "text/html", FORBIDDEN));
      resources.put(PERMISSION_DENIED, new FileResource(manager, new File(files, "error/permission-denied.html"), "text/html", FORBIDDEN));
      resources.put(CERTIFICATE_INVALID, new FileResource(manager, new File(files, "error/certificate-invalid.html"), "text/html", FORBIDDEN));
      resources.put(CERTIFICATE_REVOKED, new FileResource(manager, new File(files, "error/certificate-revoked.html"), "text/html", FORBIDDEN));
      resources.put(CERTIFICATE_UNTRUSTED, new FileResource(manager, new File(files, "error/certificate-not-trusted.html"), "text/html", FORBIDDEN));
      resources.put(CERTIFICATE_EXPIRED, new FileResource(manager, new File(files, "error/certificate-expired.html"), "text/html", FORBIDDEN));
      resources.put(CERTIFICATE_REQUIRED, new FileResource(manager, new File(files, "error/certificate-required.html"), "text/html", FORBIDDEN));
      resources.put(RESTRICTED_METHOD, new FileResource(manager, new File(files, "error/method-not-allowed.html"), "text/html", METHOD_NOT_ALLOWED));
      return resources;
   }
}