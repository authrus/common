package com.authrus.terminal.process;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TailResult {

   private Map<String, String> files;
   private String name;
}
