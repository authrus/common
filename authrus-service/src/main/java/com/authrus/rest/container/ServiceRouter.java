package com.authrus.rest.container;

import lombok.SneakyThrows;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.socket.service.PathRouter;
import org.simpleframework.http.socket.service.Router;
import org.simpleframework.http.socket.service.Service;

public class ServiceRouter extends ServiceRegistry implements Router {

   private final Router router;
   
   @SneakyThrows
   public ServiceRouter() {
      this.router = new PathRouter(services, null);
   }
  
   @Override
   public Service route(Request request, Response response) {
      return router.route(request, response);
   }
}
