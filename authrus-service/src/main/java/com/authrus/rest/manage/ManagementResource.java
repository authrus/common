package com.authrus.rest.manage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(value = ManagementResource.RESOURCE_PATH)
@Api(value = ManagementResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class ManagementResource {
   
   public static final String RESOURCE_PATH = "/v1/manage";
   public static final String RESOURCE_NAME = "manage";
   
   private final ManagementService service;
   private final ManagementClient client;
   
   @Inject
   public ManagementResource(@Context ManagementService service, @Context ManagementClient client) {
      this.service = service;
      this.client = client;
   }
   
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Get management data")
   public Response getData() {
      ManagementData data = service.getData();
      return Response.ok(data).build(); 
   }
   
   @GET
   @Path("/all")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Get management data")
   public Response getAllData() {
      List<ManagementData> data = client.getData();
      return Response.ok(data).build(); 
   }

}
