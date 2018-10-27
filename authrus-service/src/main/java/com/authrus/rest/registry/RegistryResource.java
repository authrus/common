package com.authrus.rest.registry;

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

@Path(value = RegistryResource.RESOURCE_PATH)
@Api(value = RegistryResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class RegistryResource {
   
   public static final String RESOURCE_PATH = "/v1/registry";
   public static final String RESOURCE_NAME = "registry";
   
   private final RegistryService service;
   
   @Inject
   public RegistryResource(@Context RegistryService service) {
      this.service = service;
   }
   
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Get registrations")
   public Response getNodes() {
      List<RegistryNode> nodes = service.getNodes();
      return Response.ok(nodes).build();
   }
}
