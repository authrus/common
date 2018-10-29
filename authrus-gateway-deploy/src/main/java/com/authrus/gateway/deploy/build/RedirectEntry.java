package com.authrus.gateway.deploy.build;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.simpleframework.http.Scheme;

import com.zuooh.http.proxy.resource.redirect.Redirect;

@Data
@AllArgsConstructor 
class RedirectEntry {
   
   private final Redirect redirect;
   private final Scheme scheme;
   private final String path;
}