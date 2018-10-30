package com.authrus.gateway.deploy.trace;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zuooh.http.proxy.trace.EventFilter;
import com.zuooh.http.proxy.trace.search.SearchRecorder;

public class TraceSpec {

   private final List<String> creates;
   private final List<String> updates;
   private final List<String> commits;
   
   @JsonCreator
   public TraceSpec(
         @JsonProperty("create") List<String> creates,
         @JsonProperty("update") List<String> updates,
         @JsonProperty("commit") List<String> commits)
   {
      this.creates = creates;
      this.updates = updates;
      this.commits = commits;
   }
   
   
   public SearchRecorder getRecorder() {
	   EventFilter create = new EventFilter(creates);
	   EventFilter update = new EventFilter(updates);
	   EventFilter commit = new EventFilter(commits);
	   
	   return new SearchRecorder(create, update, commit);
   }
}
