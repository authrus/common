package com.authrus.directory.user;

import com.authrus.domain.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path(value = UserResource.RESOURCE_PATH)
@Api(value = UserResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class UserResource {
   
   public static final String RESOURCE_PATH = "/v1/user";
   public static final String RESOURCE_NAME = "user";
   
   private final UserService service;
   
   @Inject
   public UserResource(@Context UserService service) {
      this.service = service;
   }
   
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Add a user")
   public Response save(User user) {
      service.save(user);
      return Response.ok(user).build(); // user with guid
   }
   
   @GET
   @Path("/guid/{guid}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Get user by guid")
   public Response findByGuid(@PathParam("guid") String guid) {
      User user = service.findByGuid(guid);
      
      if(user == null) {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(user).build();
   }
   
   @GET
   @Path("/email/{email}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Get user by email")
   public Response findByEmail(@PathParam("email") String email) {
      User user = service.findByEmail(email);
      
      if(user == null) {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(user).build();
   }
}
