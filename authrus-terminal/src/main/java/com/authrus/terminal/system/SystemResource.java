package com.authrus.terminal.system;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(value = SystemResource.RESOURCE_PATH)
@Api(value = SystemResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class SystemResource {
   
   public static final String RESOURCE_PATH = "/v1/system";
   public static final String RESOURCE_NAME = "system";

   private final SystemService service;
   
   @Inject
   public SystemResource(@Context SystemService service) {
      this.service = service;
   }
   
   @GET
   @Path("/disk")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Get host disk")
   public Response getDisk() {
      DiskResult disk = service.getDisk();
      return Response.ok(disk).build();
   }
   
   @GET
   @Path("/activity")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Get host activity")
   public Response getActivity() {
      ActivityResult activity = service.getActivity();
      return Response.ok(activity).build();
   }
}
