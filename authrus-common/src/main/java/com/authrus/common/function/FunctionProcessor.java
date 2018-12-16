package com.authrus.common.function;

import java.util.HashMap;
import java.util.Map;

public class FunctionProcessor {

   private final Map<String, FunctionParser> functions;

   public FunctionProcessor() {
      this.functions = new HashMap<String, FunctionParser>();
   }

   public FunctionParser processFunction(String function) {
      FunctionParser parser = functions.get(function);

      if(parser == null) {
         parser = new FunctionParser(function);
         functions.put(function, parser);
      }
      return parser;
   }
}
