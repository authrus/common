package com.authrus.io;

import java.io.IOException;

public interface DataReader {
   int readInt() throws IOException;
   long readLong() throws IOException;
   byte readByte() throws IOException;
   short readShort() throws IOException;
   String readString() throws IOException;
   char readChar() throws IOException;
   boolean readBoolean() throws IOException;
   float readFloat() throws IOException;
   double readDouble() throws IOException;
}
