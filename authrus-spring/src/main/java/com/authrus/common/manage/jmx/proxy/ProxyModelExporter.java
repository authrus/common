package com.authrus.common.manage.jmx.proxy;

import javax.management.MBeanException;
import javax.management.modelmbean.ModelMBean;

import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;

public class ProxyModelExporter extends AnnotationMBeanExporter {
   
   private final boolean enabled;
   
   public ProxyModelExporter() {
      this(false);
   }
   
   public ProxyModelExporter(boolean enabled) {
      this.enabled = enabled;
   }
   
   @Override
   protected ModelMBean createModelMBean() throws MBeanException {
      if(enabled) {
         return new ProxyModel();
      }
      return super.createModelMBean();
   }
}
