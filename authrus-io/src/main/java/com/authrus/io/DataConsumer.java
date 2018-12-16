package com.authrus.io;

public interface DataConsumer {
   void consume(DataReader reader) throws Exception;
}
