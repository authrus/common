package com.authrus.terminal.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiskPartition {

   private String description;
   private String name;
   private long total;
   private long usable;
   private long free;
   
   public long getUsedPercentage() {
      double maximum = total;
      double used = maximum - free;
      double ratio = used / maximum;
      
      return Math.round(ratio * 100.0);
   }
}
