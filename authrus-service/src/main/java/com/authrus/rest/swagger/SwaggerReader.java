package com.authrus.rest.swagger;

import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.DefaultReaderConfig;
import io.swagger.models.Swagger;

import java.util.Set;

public class SwaggerReader {

   public static Swagger read(Set<Class<?>> classes) {
      DefaultReaderConfig config = new DefaultReaderConfig();
      Swagger swagger = new Swagger();
      
      config.setScanAllResources(true);
      
      return new Reader(swagger, config).read(classes);
   }
}
