package com.authrus.common.mail;

public enum MailType {
   HTML("text/html", "html", "UTF-8"), 
   TEXT("text/plain", "plain", "UTF-8");

   private final String contentType;
   private final String subType;
   private final String charset;

   private MailType(String contentType, String subType, String charset) {
      this.contentType = contentType;
      this.subType = subType;
      this.charset = charset;
   }

   public String getCharset() {
      return charset;
   }

   public String getContentType() {
      return contentType;
   }

   public String getSubType() {
      return subType;
   }
}
