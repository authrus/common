package com.authrus.gateway.deploy.build;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zuooh.http.proxy.log.FileLog;
import com.zuooh.http.proxy.log.Formatter;

class AccessLog {
   
   private String path;
   private String format;
   private int threshold;
   
   @JsonCreator
   public AccessLog(
         @JsonProperty("path") String path,
         @JsonProperty("format") String format,
         @JsonProperty("threshold") int threshold)
   {
      this.path = path;
      this.format = format;
      this.threshold = threshold;
   }
   
   public FileLog createLog(String directory) {
      File file = new File(directory, path);
      Formatter formatter = new Formatter(format);
      FileLog log = new FileLog(file, formatter, threshold);
      
      return log;
   }
}