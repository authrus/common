package com.authrus.rest.content;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public interface ContentHandler {
   void handle(Request request, Response response);
}
