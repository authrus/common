package com.authrus.common.mail;

import static com.authrus.common.mail.MailRecipientType.TO;
import static java.util.Collections.EMPTY_SET;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MailMessage {

   private Set<MailAttachment> attachments;   
   private Set<MailRecipient> recipients;
   private MailAddress from;
   private MailType type;
   private String subject;
   private String body;

   public MailMessage(MailType type, MailAddress from, String subject, String body) {
      this(type, from, EMPTY_SET, subject, body);
   }  
   
   public MailMessage(MailType type, MailAddress from, String recipient, String subject, String body) {
      this(type, from, TO.getRecipients(recipient), subject, body);
   }   
   
   public MailMessage(MailType type, MailAddress from, MailRecipient recipient, String subject, String body) {
      this(type, from, Collections.singleton(recipient), subject, body);
   }
   
   public MailMessage(MailType type, MailAddress from, Set<MailRecipient> recipients, String subject, String body) {
      this.recipients = new TreeSet<MailRecipient>(recipients);
      this.attachments = new LinkedHashSet<MailAttachment>();
      this.from = new MailAddress(from);
      this.subject = subject;   
      this.type = type;
      this.body = body;   
   }   

   public MailMessage getMessage() {
      MailAddress address = new MailAddress(from);
      MailMessage mail = new MailMessage(type, address, recipients, subject, body);
      
      for(MailAttachment attachment : attachments) {
         mail.addAttachment(attachment);
      }      
      return mail;
   }      

   public MailAddress getFrom() {
      return from;
   }
   
   public void setFrom(String mail) {
      from.setMail(mail);
   }
   
   public String getSubject() {
      return subject;
   }
   
   public void setSubject(String subject) {
      this.subject = subject;
   }
   
   public Set<MailRecipient> getRecipients() {
      return recipients;
   }

   public void setRecipient(MailRecipient recipient) {
      recipients.clear();
      recipients.add(recipient);
   }
   
   public void setRecipients(List<MailRecipient> list) {
      recipients.clear();
      recipients.addAll(list);
   }   

   public void addRecipient(MailRecipient recipient) {
      recipients.add(recipient);
   }   
   
   public void addRecipients(List<MailRecipient> list) {
      recipients.addAll(list);
   }   
   
   public void setRecipient(MailRecipientType type, String mail) {
      setRecipient(type.getRecipient(mail));
   }   
   
   public void addRecipient(MailRecipientType type, String mail) {
      addRecipient(type.getRecipient(mail));
   }      

   public Set<MailAttachment> getAttachments() {
      return attachments;
   }   
   
   public void setAttachment(MailAttachment attachment) {
      attachments.clear();
      attachments.add(attachment);
   } 
   
   public void setAttachments(List<MailAttachment> list) {
      attachments.clear();
      attachments.addAll(list);
   }   

   public void addAttachment(MailAttachment attachment) {
      attachments.add(attachment);
   }
   
   public void addAttachments(List<MailAttachment> list) {
      attachments.addAll(list);
   }   

   public void setType(MailType type) {
      this.type = type;
   }

   public MailType getType() {
      return type;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public String getBody() {
      return body;
   }
}
