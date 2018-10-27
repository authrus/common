package com.authrus.rest.status;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = PingResource.RESOURCE_PATH)
@Api(value = PingResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class PingResource {
   
   public static final String RESOURCE_PATH = "/v1/ping";
   public static final String RESOURCE_NAME = "ping";
   
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Check status")
   public PingResult ping() {
      return PingResult.builder()
            .message("Everything is ok")
            .healthy(true)
            .build();
   }
   
}
