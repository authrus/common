package com.authrus.common.mail;

import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

public class ByteArrayAttachment implements MailAttachment {

   protected final DataSource source;
   protected final String name;
   protected final byte[] data;
   protected final int size;

   public ByteArrayAttachment(byte[] data, String name, String type) {
      this.source = new ByteArrayDataSource(data, type);
      this.size = data.length;
      this.data = data;
      this.name = name;
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
      return size;
   }
}
