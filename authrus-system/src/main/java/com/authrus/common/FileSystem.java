package com.authrus.common;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileSystem {
   long freeExternalSpace();
   long sizeOfFile(String fileName);
   long sizeOfExternalFile(String file);
   long lastModified(String file);
   long lastExternalModified(String file);
   boolean touchExternalFile(String file);
   InputStream openFile(String file);
   byte[] loadFile(String file);
   OutputStream createExternalFile(String file);
   InputStream openExternalFile(String file);
   byte[] loadExternalFile(String file);
   String[] listExternalFiles(String filter);
   String[] listFiles(String filter);
   boolean deleteExternalFile(String file);
   boolean deleteFile(String file);
}
