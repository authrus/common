package com.authrus.common.mail;

public class MailRecipient implements Comparable<MailRecipient> {

   private final MailRecipientComparator comparator;
   private final MailRecipientType type;
   private final MailAddress address;

   public MailRecipient(MailRecipientType type, String mail) {
      this.comparator = new MailRecipientComparator();
      this.address = new MailAddress(mail);
      this.type = type;
   }
   
   public String getMail() {
      return address.getMail();
   }
   
   public MailAddress getAddress() {
      return address;
   }   
   
   public MailRecipientType getType() {
      return type;
   }   

   @Override
   public int compareTo(MailRecipient other) {
      return comparator.compare(this, other); 
   }   
   
   @Override
   public String toString() {
      return String.format("%s: %s", type, address);
   }
}
