package com.authrus.rest.container;

import com.authrus.common.AnnotationPresentScanner;
import com.authrus.common.ClassPathScanner;
import com.authrus.rest.manage.ManagementResource;
import com.authrus.rest.registry.RegistryResource;
import com.authrus.rest.status.PingResource;
import com.authrus.rest.swagger.SwaggerResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

import java.util.Set;

import javax.ws.rs.Path;

import lombok.SneakyThrows;

public class ResourceScanner {

   private static String RESOURCE_SUFFIX = "Resource";
   
   private final ClassPathScanner scanner;
   
   public ResourceScanner(String packages) {
      this.scanner = new AnnotationPresentScanner(Path.class, RESOURCE_SUFFIX, packages);
   }
   
   @SneakyThrows
   public Set<Class<?>> scan(boolean swagger) {
      Set<Class<?>> types = scanner.scan();
      
      types.add(PingResource.class);
      types.add(RegistryResource.class);
      types.add(ManagementResource.class);
      
      if(swagger) {
         types.add(SwaggerResource.class);
         types.add(SwaggerSerializers.class);
      } else {
         types.remove(SwaggerResource.class);
      }
      return types;
   }
}
