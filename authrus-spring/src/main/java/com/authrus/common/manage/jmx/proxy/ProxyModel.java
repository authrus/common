package com.authrus.common.manage.jmx.proxy;

import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.ModelMBeanInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.SpringModelMBean;

public class ProxyModel extends SpringModelMBean {
   
   private static final Logger LOG = LoggerFactory.getLogger(ProxyModel.class);

   public ProxyModel() throws MBeanException, RuntimeOperationsException {
      super();
   }
   
   public ProxyModel(ModelMBeanInfo info) throws MBeanException, RuntimeOperationsException {
      super(info);
   }
   
   public Object invoke(String name, Object[] arguments, String[] signature) throws MBeanException, ReflectionException {
      try {
         return super.invoke(name, arguments, signature);
      } catch(MBeanException cause) {
         LOG.info("Error invoking " + name, cause);
         throw cause;
      } catch(ReflectionException cause) {
         LOG.info("Error invoking " + name, cause);
         throw cause;
      }      
   }
}
