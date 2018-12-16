package com.authrus.message.serialize;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.authrus.common.io.Base64InputStream;
import com.authrus.common.io.Base64OutputStream;

public class BinarySerializer {

   private final int buffer;

   public BinarySerializer() {
      this(2048);
   }

   public BinarySerializer(int buffer) {
      this.buffer = buffer;
   }

   public String toString(Object value) throws Exception {
      OutputStream encoder = new Base64OutputStream(buffer);
      ObjectOutput serializer = new ObjectOutputStream(encoder);

      serializer.writeObject(value);
      serializer.close();

      return encoder.toString();
   }

   public Object fromString(String value) throws Exception {
      InputStream decoder = new Base64InputStream(value);
      ObjectInput deserializer = new ObjectInputStream(decoder);

      return deserializer.readObject();
   }

}
