package com.authrus.store.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.zuooh.tuple.Tuple;

@Path(value = TupleResource.RESOURCE_PATH)
@Api(value = TupleResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class TupleResource {
   
   public static final String RESOURCE_PATH = "/v1/tuple";
   public static final String RESOURCE_NAME = "tuple";
   
   private final TupleService service;
   
   @Inject
   public TupleResource(@Context TupleService service) {
      this.service = service;
   }
   
   @GET
   @Path("/{type}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Show all tuples")
   public Response getTuples(@PathParam("type") String type) {
      List<Tuple> tuples = service.getTuples(type);
      return Response.ok(tuples).build(); // user with guid
   }
}
