package com.authrus.attribute.transform;

import java.io.File;

public class FileTransform implements ObjectTransform<File, String> {

   public File toObject(String path) {
      return new File(path);
   }

   public String fromObject(File path) {
      return path.getAbsolutePath();
   }
}
