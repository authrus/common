package com.authrus.common.mail;

import java.io.File;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

public class FileAttachment implements MailAttachment {

   protected final DataSource source;
   protected final String name;
   protected final File file;

   public FileAttachment(File file, String name) {
      this.source = new FileDataSource(file);
      this.name = name;
      this.file = file;
   }

   @Override
   public MimeBodyPart getPart() throws Exception {
      MimeBodyPart body = new MimeBodyPart();
      DataHandler handler = new DataHandler(source);

      body.setDataHandler(handler);
      body.setFileName(name);
      body.setDisposition(Part.ATTACHMENT);

      return body;
   }
   
   @Override
   public InputStream getData() throws Exception {
      return source.getInputStream();
   }

   @Override
   public String getContentType() throws Exception {
      return source.getContentType();
   }

   @Override
   public String getName() throws Exception {
      return name;
   }

   @Override
   public long getSize() throws Exception {
      return file.length();
   }
}
