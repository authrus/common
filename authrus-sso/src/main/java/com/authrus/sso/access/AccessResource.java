package com.authrus.sso.access;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Path(value = AccessResource.RESOURCE_PATH)
@Api(value = AccessResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class AccessResource {
   
   public static final String RESOURCE_PATH = "/v1/access";
   public static final String RESOURCE_NAME = "access";
   
   private final AccessService service;
   
   @Inject
   public AccessResource(@Context AccessService service) {
      this.service = service;
   }

   @GET
   @Path("/grant/list")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "All grants")
   public Response listGrants(@PathParam("token") String token) {
      List<AccessGrant> requests = service.listGrants();
      return Response.ok(requests).build();
   }
   
   @GET
   @Path("/request/list")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "All requests")
   public Response listRequests() {
      List<AccessRequest> requests = service.listRequests();
      return Response.ok(requests).build();
   }
   
   @GET
   @Path("/grant/{token}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Grant access for token")
   public Response getAccess(@PathParam("token") String token) {
      return grantAccess(token);
   }
   
   @POST
   @Path("/grant/{token}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Grant access for token")
   public Response grantAccess(@PathParam("token") String token) {
      AccessGrant grant = service.grantAccess(token);
      
      if(grant != null) {
         NewCookie cookie = grant.getCookie();
         String redirect = grant.getRedirect();
         URI address = URI.create(redirect);
         
         return Response.temporaryRedirect(address)
               .cookie(cookie)
               .build();
      }
      URI address = URI.create("/login/invalid-token");
      return Response.temporaryRedirect(address).build();
   }

   @GET
   @Path("/grant/code")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Grant access for code")
   public Response getAccess(@QueryParam("code") int code) {
      return grantAccess(code);
   }
   
   @POST
   @Path("/grant/code")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Grant access for code")
   public Response grantAccess(@QueryParam("code") int code) {
      AccessGrant grant = service.grantAccess(code);
      
      if(grant != null) {
         NewCookie cookie = grant.getCookie();
         String redirect = grant.getRedirect();
         URI address = URI.create(redirect);
         
         return Response.temporaryRedirect(address)
               .cookie(cookie)
               .build();
      }
      URI address = URI.create("/login/invalid-code");
      return Response.temporaryRedirect(address).build();
   }
   
   @GET
   @Path("/verify/{token}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Verify a token")
   public Response verifyAccess(@PathParam("token") String token) {
      List<String> result = Collections.singletonList(token);
      boolean verified = service.verifyAccess(token);
      
      if(verified) {
         return Response.ok(result).build();
      }
      return Response.status(Response.Status.NOT_FOUND).build();
   }
}
