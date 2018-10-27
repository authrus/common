package com.authrus.store.resource;

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

@Path(value = SessionResource.RESOURCE_PATH)
@Api(value = SessionResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class SessionResource {
   
   public static final String RESOURCE_PATH = "/v1/session";
   public static final String RESOURCE_NAME = "session";
   
   private final SessionService service;
   
   @Inject
   public SessionResource(@Context SessionService service) {
      this.service = service;
   }
   
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Show sessions")
   public Response getSessions() {
      List<SessionResult> list = service.getSessions();
      return Response.ok(list).build(); // user with guid
   }
}
