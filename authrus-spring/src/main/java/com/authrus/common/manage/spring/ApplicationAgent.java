package com.authrus.common.manage.spring;

public interface ApplicationAgent {
   String getWorkingDirectory();
   String getUpTime();
   String getHostTime();
   String getHostTimeZone();
   String getHostName();
   String getMemoryCapacity();
   String getMemoryPercentageUsed();
   String getProcessOwner();
}
