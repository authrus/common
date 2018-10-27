package com.authrus.rest.swagger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import com.authrus.rest.container.ResourceScanner;
import io.swagger.models.Swagger;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path(value = SwaggerResource.RESOURCE_PATH)
public class SwaggerResource {

   public static final String RESOURCE_PATH = "/v1/swagger";
   
   private final ResourceScanner scanner;
   
   @Inject
   public SwaggerResource(ResourceScanner scanner) {
      this.scanner = scanner;
   }
   
   @GET
   @Path("/api.json")
   @Produces(APPLICATION_JSON)
   public Swagger list() throws Exception {
      Set<Class<?>> types = scanner.scan(false);
      return SwaggerReader.read(types);
   }
}
