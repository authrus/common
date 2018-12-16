package com.authrus.tuple.frame;

class SessionContext {

   private final SessionPublisher publisher;
   private final Session session;
   
   public SessionContext(SessionPublisher publisher, Session session) {
      this.publisher = publisher;
      this.session = session;      
   }
   
   public SessionPublisher getPublisher() {
      return publisher;
   }
   
   public Session getSession() {
      return session;
   }
}
