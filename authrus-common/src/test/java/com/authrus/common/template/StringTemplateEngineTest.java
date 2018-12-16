package com.authrus.common.template;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class StringTemplateEngineTest extends TestCase {
   
   private static final int ITERATIONS = 100000;
   
   private static class SomeBean {
      
      private String name;
      private int age;
      
      public SomeBean(String name, int age) {
         this.name = name;
         this.age = age;
      }
      
      public int getAge(){
         return age;
      }
      
      public String getName() {
         return name;
      }
   }
   
   public void testTemplateEngine() throws Exception {
      Map<String, Object> map = new HashMap<String, Object>();
      TemplateModel model = new TemplateModel(map);
      StringTemplateEngine engine = new StringTemplateEngine();
      SomeBean bean = new SomeBean("niall", 36);
      
      map.put("bean", bean);
      map.put("ss", "Hello World");
      
      System.err.println(engine.renderTemplate(model, "x=${ss} ${person.mail} ${bean.age} ${bean.test} dd"));
      System.err.println(engine.renderTemplate(model, "x=${ss} ${person.mail} ${bean.age} ${bean.test}"));
      double start = System.currentTimeMillis();
      
      for(int i = 0; i < ITERATIONS; i++) {
         engine.renderTemplate(model, "x=${ss} ${person.mail} ${bean.age} ${bean.test}");
      }
      double end = System.currentTimeMillis();
      double duration = end - start;
      
      System.err.println("Time taken for " + ITERATIONS + " renders is " + duration + " ms, thats " + (duration / ITERATIONS) + " ms for each template");
   }

}
