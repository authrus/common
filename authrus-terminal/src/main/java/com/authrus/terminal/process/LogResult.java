package com.authrus.terminal.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LogResult {

   private String name;
   private String log;
}
