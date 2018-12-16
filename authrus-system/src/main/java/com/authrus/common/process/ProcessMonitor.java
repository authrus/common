package com.authrus.common.process;

import java.util.List;

public interface ProcessMonitor {
   String getDeviceName();
   String getDevicePlatform();
   String getDeviceVersion();
   ProcessMemory getProcessMemory();
   List<ProcessResource> getProcessResources();
}
