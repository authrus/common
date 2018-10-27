package com.authrus.sso.mail;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(value = MailResource.RESOURCE_PATH)
@Api(value = MailResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class MailResource {
   
   public static final String RESOURCE_PATH = "/v1/mail";
   public static final String RESOURCE_NAME = "mail";
   
   private final MailService service;
   
   @Inject
   public MailResource(@Context MailService service) {
      this.service = service;
   }
   
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Sent a mail")
   public Response send(MailRequest request) {
      String from = request.getFrom();
      String to = request.getTo();
      String subject = request.getSubject();
      String message = request.getMessage();
      
      service.send(from, to, subject, message);
      return Response.ok(request).build();
   }
}
