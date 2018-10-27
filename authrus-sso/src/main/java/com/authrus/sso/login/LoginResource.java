package com.authrus.sso.login;

import com.authrus.domain.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(value = LoginResource.RESOURCE_PATH)
@Api(value = LoginResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class LoginResource {
   
   public static final String RESOURCE_PATH = "/v1/login";
   public static final String RESOURCE_NAME = "login";
   
   private final LoginService service;
   
   @Inject
   public LoginResource(@Context LoginService service) {
      this.service = service;
   }
   
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @ApiOperation(value = "Authenticate a user")
   public Response login(
         @QueryParam("email") String email,
         @QueryParam("password") String password,
         @QueryParam("redirect") String redirect,
         @QueryParam("remember") Boolean remember,
         @QueryParam("type") String type) 
   {
      Login login = service.login(email, password, redirect, type);  
      URI address = login.getRedirect();
      
      return Response.temporaryRedirect(address).build();
   }

   
   @POST
   @Path("/register")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @ApiOperation(value = "Register a new user")
   public Response register(
         @QueryParam("email") String email,
         @QueryParam("password") String password,
         @QueryParam("firstName") String firstName,
         @QueryParam("lastName") String lastName,
         @QueryParam("company") String company,
         @QueryParam("redirect") String redirect,
         @QueryParam("remember") Boolean remember,
         @QueryParam("type") String type) 
   {
      User user = User.builder()
            .email(email)
            .password(password)
            .firstName(firstName)
            .lastName(lastName)
            .company(company)
            .build();
      Login login = service.register(user, redirect, type);
      URI address = login.getRedirect();
      
      return Response.temporaryRedirect(address).build();
   }
   
}
