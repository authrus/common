package com.authrus.common.file;

public class FileNameCreator {

   private final String filePrefix;
   private final String fileSuffix;

   public FileNameCreator(String filePrefix, String fileSuffix) {
      this.filePrefix = encodeToHex(filePrefix);
      this.fileSuffix = fileSuffix;
   }
   
   private String encodeToHex(String someString) {
      StringBuilder builder = new StringBuilder();

      if(someString == null || someString.length() == 0) {
         throw new IllegalStateException("Can not create file name with empty string");
      }
      byte[] stringBytes = someString.getBytes();
      
      for(int i = 0; i < stringBytes.length; i++) {
         byte octet = stringBytes[i];
         String code = Integer.toHexString(octet);
         int length = code.length();

         if(length == 1) {
            builder.append("0");
         }
         builder.append(code);
      }
      return builder.toString();
   }

   public boolean acceptFileName(String fileName) { 
      if(fileName.startsWith(filePrefix)) {
         int filePrefixLength = filePrefix.length();
         int fileNameLength = fileName.length();
         
         if(filePrefixLength < fileNameLength) {
            if(fileName.charAt(filePrefixLength) == '_') {         
               return fileName.endsWith(fileSuffix);
            }
         }
      }
      return false;
   }

   public String encodeToFileName(String someString) {
      return filePrefix + "_" + encodeToHex(someString) + fileSuffix;
   }

   public String decodeFileNameToString(String fileName) {
      int prefixLength = filePrefix.length() + 1;
      int suffixIndex = fileName.indexOf(fileSuffix);
      String fileNameWithoutSuffix = fileName.substring(0, suffixIndex);
      String fileNameWithoutPrefix = fileNameWithoutSuffix.substring(prefixLength);
      int length = fileNameWithoutPrefix.length();

      if(length == 0) {
         throw new IllegalStateException("Game name cannot be extracted from an empty string");
      }
      StringBuilder decodedName = new StringBuilder();

      for(int i = 0; i < length; i += 2) {
         String hexCode = fileNameWithoutPrefix.substring(i, i + 2);
         char character = (char)Integer.parseInt(hexCode, 16);

         decodedName.append(character);
      }
      return decodedName.toString();
   }
}
