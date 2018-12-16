package com.authrus.common.process;

public interface ProcessMemory {
   long getHeapMemory();
   long getTotalMemory();
   long getSharedMemory();
   long getPrivateMemory();
   void dumpMemory(String file);
}

