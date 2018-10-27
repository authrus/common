package com.authrus.gateway.health;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = HealthResource.RESOURCE_PATH)
@Api(value = HealthResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class HealthResource {
   
   public static final String RESOURCE_PATH = "/v1/health";
   public static final String RESOURCE_NAME = "health";
   
   private final HealthService service;
   
   @Inject
   public HealthResource(HealthService service) {
      this.service = service;
   }
   
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Show health checks")
   public Map<String, String> health() {
      return service.health();
   }
}
