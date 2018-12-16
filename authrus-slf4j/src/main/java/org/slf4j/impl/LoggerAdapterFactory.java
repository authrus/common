package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

class LoggerAdapterFactory implements ILoggerFactory {

   public Logger getLogger(String name) {
      return new LoggerAdapter(name);
   }
}