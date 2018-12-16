package com.authrus.common.mail;

import java.io.InputStream;

import javax.mail.internet.MimeBodyPart;

public interface MailAttachment {
   MimeBodyPart getPart() throws Exception;
   InputStream getData() throws Exception;
   String getContentType() throws Exception;
   String getName() throws Exception;
   long getSize() throws Exception;
}
