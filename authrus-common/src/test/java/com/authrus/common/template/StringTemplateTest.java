package com.authrus.common.template;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.common.reflect.PropertyBinder;

public class StringTemplateTest extends TestCase {
   
   public void testTemplate() throws Exception {
      StringTemplate template = new StringTemplate("${a} is ${A}");
      PropertyBinder binder = new PropertyBinder();
      Map<String, Object> map = new LinkedHashMap<String, Object>();
      TemplateModel model = new TemplateModel(map);
      PropertyTemplateFilter filter = new PropertyTemplateFilter(model, binder);
      StringWriter writer = new StringWriter();
      
      map.put("a", "<This is a>");
      map.put("b", "<This is b>");
      template.render(filter, writer);
      
      System.err.println(writer);
      
   }

}
