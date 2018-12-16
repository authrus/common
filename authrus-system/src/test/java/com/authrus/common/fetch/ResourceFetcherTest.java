package com.authrus.common.fetch;

import junit.framework.TestCase;

public class ResourceFetcherTest extends TestCase {
   
   public void testPost() throws Exception {
      ResourceFetcher fetcher = new ResourceFetcher("POST", "https://www.zuooh.com");
      
      fetcher.append("name", "blah");
      fetcher.append("blah", "foo");
      
      String result = fetcher.fetch(String.class);
      
      System.err.println(result);
   }
   
   public void testGet() throws Exception {
      ResourceFetcher fetcher = new ResourceFetcher("GET", "http://www.google.com");
      String result = fetcher.fetch(String.class);
      
      System.err.println(result);
   }

}
