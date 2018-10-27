package com.authrus.sso;

import com.authrus.rest.EnableResourceServer;
import com.authrus.sso.access.EnableAccessControl;
import com.authrus.sso.login.EnableLogin;
import com.authrus.store.EnableDataStore;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@EnableLogin
@EnableDataStore
@EnableAccessControl
@EnableResourceServer
@SpringBootApplication
public class IdentityApplication {

   public static void main(String[] list) throws Exception {
      SpringApplicationBuilder builder = new SpringApplicationBuilder(IdentityApplication.class);
      builder.web(WebApplicationType.NONE).run(list);
   }
}
