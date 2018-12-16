package com.authrus.common.template;

import java.io.StringWriter;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.common.reflect.PropertyBinder;

public class StringTemplateEngine implements TemplateEngine {
   
   private final Cache<String, Template> cache;
   private final PropertyBinder binder;
   
   public StringTemplateEngine() {
      this(100);
   }
   
   public StringTemplateEngine(int capacity) {
      this.cache = new LeastRecentlyUsedCache<String, Template>(capacity);
      this.binder = new PropertyBinder();
   }   

   @Override
   public String renderTemplate(TemplateModel model, String source) throws Exception {      
      TemplateFilter filter = createFilter(model);
      Template template = resolveTemplate(source);
      
      try {
         StringWriter writer = new StringWriter();
         
         if(template != null) {
            template.render(filter, writer);
         }
         return writer.toString();
      } catch(Exception e) {
         throw new IllegalStateException("Could not render template '" + source + "'", e);
      }
   }   
   
   @Override
   public boolean validTemplate(String source) throws Exception {
      return true;
   }      
   
   protected Template resolveTemplate(String source) throws Exception {
      Template template = cache.fetch(source);
      
      if(template == null) {
         template = createTemplate(source);
         cache.cache(source, template);
      }
      return template;
   }
   
   protected Template createTemplate(String source) throws Exception {
      return new StringTemplate(source);
   }
   
   protected TemplateFilter createFilter(TemplateModel model) throws Exception {
      return new PropertyTemplateFilter(model, binder);
   }
}

