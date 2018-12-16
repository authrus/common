package com.authrus.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemProperties extends Properties {
   
   private static final Logger LOG = LoggerFactory.getLogger(FileSystemProperties.class);

   private final FileSystem fileSystem;
   private final String propertiesFile;

   public FileSystemProperties(FileSystem fileSystem, String propertiesFile) {
      this.propertiesFile = propertiesFile;
      this.fileSystem = fileSystem;
   }

   public void save() {
      try {
         OutputStream source = fileSystem.createExternalFile(propertiesFile);
         store(source, null);
      } catch(Exception e) {
         LOG.info("Could not store file " + propertiesFile, e);
      }
   }

   public void refresh() {
      try {
         InputStream source = fileSystem.openFile(propertiesFile);
         load(source);
      } catch(Exception e) {
         try {
            long size = fileSystem.sizeOfExternalFile(propertiesFile);
            
            if(size > 0) {
               InputStream externalSource = fileSystem.openExternalFile(propertiesFile);
               load(externalSource);
            }
         } catch(Exception error) {
            LOG.info("Could not load file " + propertiesFile, error);
         }
      }
   }

}
