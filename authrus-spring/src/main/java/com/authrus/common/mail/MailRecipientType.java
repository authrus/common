package com.authrus.common.mail;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public enum MailRecipientType {
   TO,
   CC,
   BCC;
   
   public boolean isTo() {
      return this == TO;
   }
   
   public boolean isCarbonCopy() {
      return this == CC;
   }
   
   public boolean isBlindCarbonCopy() {
      return this == BCC;
   }
   
   public MailRecipient getRecipient(String mail) {
      return  new MailRecipient(this, mail);
   }
   
   public Set<MailRecipient> getRecipients(String mail) {      
      String[] values = mail.split("\\s*,\\s*");
      
      if(values.length > 0) {
         Set<MailRecipient> recipients = new TreeSet<MailRecipient>();
         
         for(String value : values) {
            MailRecipient recipient = getRecipient(value);
           
            if(!mail.isEmpty()) {
               recipients.add(recipient);
            }
         }
         return recipients;
      }
      return Collections.emptySet();
   }
}
