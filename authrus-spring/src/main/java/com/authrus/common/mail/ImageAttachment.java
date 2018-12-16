package com.authrus.common.mail;

import java.io.File;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

public class ImageAttachment extends FileAttachment {

   public ImageAttachment(File file, String name) {
      super(file, name);
   }

   @Override
   public MimeBodyPart getPart() throws MessagingException {
      MimeBodyPart body = new MimeBodyPart();
      DataHandler handler = new DataHandler(source);

      body.setDataHandler(handler);
      body.setContentID("<" + name + ">");
      body.setFileName(name);      
      body.setDisposition(Part.INLINE);

      return body;
   }
}
