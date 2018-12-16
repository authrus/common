package com.authrus.common.reflect;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Locale;

import junit.framework.TestCase;

public class PropertyMapperTest extends TestCase {
   public static class ExampleObjectToCheck{
      public String getName(){
         return "me";
      }
      public String variableArgs(String text, Integer number, Locale... locale) {
         return "ok";
      }
      public String doSomething(String text, int val) {
         return "x";
      }
      public String allVar(Object... list){
         return "y";
      }
   }
   
   public void testVariableArgumentOnlyMethod() throws Exception {
      PropertyMapper mapper = new PropertyMapper();
      ExampleObjectToCheck object = new ExampleObjectToCheck();
      Method printf = mapper.getMethodMatch(ExampleObjectToCheck.class, "allVar", String.class);
      
      printf.invoke(object, new Object[]{new Object[]{"a"}});
   }
   
   public void testPrintStream() throws Exception {
      PropertyMapper mapper = new PropertyMapper();
      Method printf = mapper.getMethodMatch(PrintStream.class, "printf", String.class, String.class, String.class, String.class, String.class);
      
      printf.invoke(System.err, "registerProfile(1=%s,2=%s,3=%s,4=%s,5=%s)%n", new Object[]{"a", "b", "c", "d", "e"});
   }
   
   public void testPropertyMapper() throws Exception {
      PropertyMapper mapper = new PropertyMapper();
      Method method = mapper.getProperty(ExampleObjectToCheck.class, "name");
      System.err.println(method);
      
      Method variableMethod = mapper.getMethodMatch(ExampleObjectToCheck.class, "variableArgs", String.class, Number.class);
      assertNotNull(variableMethod);
      System.err.println(variableMethod);
      
      Method variableMethod2 = mapper.getMethodMatch(ExampleObjectToCheck.class, "variableArgs", String.class, Number.class, Locale.class);
      ExampleObjectToCheck example = new ExampleObjectToCheck();
      
      assertNotNull(variableMethod2);
      System.err.println(variableMethod2);
      System.err.println(example.variableArgs("text", 12, Locale.getDefault()));
      
      Method variableMethod3 = mapper.getMethodMatch(ExampleObjectToCheck.class, "variableArgs", String.class, Number.class, Locale.class, Locale.class);
      assertNotNull(variableMethod3);
      System.err.println(variableMethod3);
      
      Method variableMethod4 = mapper.getMethodMatch(ExampleObjectToCheck.class, "variableArgs", String.class, Number.class, String.class);
      assertNull(variableMethod4);
      System.err.println(variableMethod4);
      
      Method normalMethod = mapper.getMethodMatch(ExampleObjectToCheck.class, "doSomething", String.class, int.class);
      assertNotNull(normalMethod);
      System.err.println(normalMethod);
   }

}
