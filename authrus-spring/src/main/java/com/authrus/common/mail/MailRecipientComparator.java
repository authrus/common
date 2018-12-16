package com.authrus.common.mail;

import java.util.Comparator;

public class MailRecipientComparator implements Comparator<MailRecipient> {

   @Override
   public int compare(MailRecipient left, MailRecipient right) {
      MailAddress leftAddress = left.getAddress();
      MailAddress rightAddress = right.getAddress();
      
      if(leftAddress != null && rightAddress != null) {
         String leftMail = leftAddress.getMail();
         String rightMail = rightAddress.getMail();
         
         if(!leftMail.equalsIgnoreCase(rightMail)) {
            return leftMail.compareTo(rightMail); 
         }
      }
      return 0;
   }

}
