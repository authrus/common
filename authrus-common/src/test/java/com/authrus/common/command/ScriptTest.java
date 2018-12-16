package com.authrus.common.command;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.common.command.Console;
import com.authrus.common.command.Environment;
import com.authrus.common.command.MapEnvironment;
import com.authrus.common.command.Script;

public class ScriptTest extends TestCase {

   public void testScript() throws Exception {
      try {
         Map<String, String> variables = new HashMap<String, String>();
         String home = System.getProperty("java.home");
         variables.put("JAVA_HOME", home);
         Environment environment = new MapEnvironment(variables);
         File directory = new File(".");
         Script script = new Script("cmd /c java.exe -version", directory, true);
         Console console = script.execute(environment);
         String value = console.readAll();

         System.err.println(value);
         assertNotNull(value);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
