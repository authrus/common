package com.authrus.tuple.frame;

import java.nio.ByteOrder;

interface FrameHeader {
   int getProlog();
   int getVersion();
   ByteOrder getOrder();
   FrameType getType();
   int getSequence();
   int getLength();
}
