package com.authrus.gateway.status;

import static com.zuooh.http.proxy.core.State.SERVICE_AVAILABLE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = StatusResource.RESOURCE_PATH)
@Api(value = StatusResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class StatusResource {
   
   public static final String RESOURCE_PATH = "/v1/status";
   public static final String RESOURCE_NAME = "status";
   
   private final StatusService service;
   
   @Inject
   public StatusResource(StatusService service) {
      this.service = service;
   }
   
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Show all details")
   public List<StatusResult> status() {
      return service.status((state) -> true);
   }
   
   @GET
   @Path("/success")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Show successful details")
   public List<StatusResult> statusSuccess() {
      return service.status((state) -> state == SERVICE_AVAILABLE);
   }
   
   @GET
   @Path("/error")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Show error details")
   public List<StatusResult> statusError() {
      return service.status((state) -> state != SERVICE_AVAILABLE);
   }
   
   @GET
   @Path("/complete")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Show complete details")
   public List<StatusResult> statusComplete() {
      return service.statusComplete((state) -> true);
   }

}
