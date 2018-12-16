package com.authrus.common.io;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class Base64Marshaller {

   public String write(Object remote) throws Exception {
      OutputStream encoder = new Base64OutputStream(1024);
      ObjectOutput serializer = new ObjectOutputStream(encoder);

      serializer.writeObject(remote);
      serializer.close();

      return encoder.toString();
   }

   public <T> T read(String value) throws Exception {
      InputStream decoder = new Base64InputStream(value);
      ObjectInput deserializer = new ObjectInputStream(decoder);

      return (T) deserializer.readObject();
   }
}
