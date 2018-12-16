package com.authrus.common.socket.proxy.analyser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Packet {

   private String connection;
   private byte[] buffer;
   private int count;
   private int off;

   public Packet(String connection, byte[] buffer) {
      this(connection, buffer, 0, buffer.length);
   }

   public Packet(String connection, byte[] buffer, int off, int count) {
      this.connection = connection;
      this.buffer = buffer;
      this.count = count;
      this.off = off;
   }

   public InputStream open() {
      if (buffer == null) {
         throw new IllegalStateException("Packet has been disposed of");
      }
      return new ByteArrayInputStream(buffer, off, count);
   }

   public Packet copy() {
      if (buffer == null) {
         throw new IllegalStateException("Packet has been disposed of");
      }
      byte[] payload = new byte[count - off];

      if (payload.length > 0) {
         System.arraycopy(buffer, off, payload, 0, payload.length);
      }
      return new Packet(connection, payload);
   }

   public byte[] extract() {
      if (buffer == null) {
         throw new IllegalStateException("Packet has been disposed of");
      }
      byte[] payload = new byte[count - off];

      if (payload.length > 0) {
         System.arraycopy(buffer, off, payload, 0, payload.length);
      }
      return payload;
   }

   public int length() {
      if (buffer == null) {
         throw new IllegalStateException("Packet has been disposed of");
      }
      return count - off;
   }

   public String connection() {
      return connection;
   }

   public void dispose() {
      buffer = null;
      count = 0;
      off = 0;
   }

   @Override
   public String toString() {
      return String.format("%s from %s", (count - off), connection);
   }
}
