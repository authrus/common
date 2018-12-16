package com.authrus.io;

import java.nio.ByteBuffer;

public interface DataDispatcher {
   void dispatch(ByteBuffer buffer) throws Exception;
}
