package com.authrus.gateway.rules;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = AccessRuleResource.RESOURCE_PATH)
@Api(value = AccessRuleResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class AccessRuleResource {
   
   public static final String RESOURCE_PATH = "/v1/rule";
   public static final String RESOURCE_NAME = "rule";
   
   private final AccessRuleService service;
   
   @Inject
   public AccessRuleResource(AccessRuleService service) {
      this.service = service;
   }
   
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Show all rules")
   public List<AccessRule> rules() {
      return service.rules();
   }
}
