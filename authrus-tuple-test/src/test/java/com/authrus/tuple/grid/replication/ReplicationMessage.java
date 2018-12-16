package com.authrus.tuple.grid.replication;

import java.io.Serializable;

public class ReplicationMessage implements Serializable {

   private static final long serialVersionUID = 1L;
   
   public final String name;
   public final String job;
   public final String address;
   public final int age;
   public final long update;
   
   public ReplicationMessage(String name, String job, String address, int age, long update) {
      this.name = name;
      this.job = job;
      this.address = address;
      this.age = age;
      this.update = update;      
   }
}
