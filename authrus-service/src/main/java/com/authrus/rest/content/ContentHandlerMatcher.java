package com.authrus.rest.content;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public interface ContentHandlerMatcher {
   ContentHandler match(Request request, Response response);
}
