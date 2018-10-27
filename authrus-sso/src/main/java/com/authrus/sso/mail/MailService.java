package com.authrus.sso.mail;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import com.zuooh.common.mail.MailAddress;
import com.zuooh.common.mail.MailClient;
import com.zuooh.common.mail.MailMessage;
import com.zuooh.common.mail.MailRecipient;
import com.zuooh.common.mail.MailRecipientType;
import com.zuooh.common.mail.MailType;

@Component
@AllArgsConstructor
public class MailService {
   
   private final MailClient client;

   public void send(String from, String to, String subject, String body) {
      MailAddress source = new MailAddress(from);
      MailRecipient destination = new MailRecipient(MailRecipientType.TO, to);
      MailMessage message = new MailMessage(MailType.TEXT, source, destination, subject, body);
   
      client.sendMessage(message);
   }
}
