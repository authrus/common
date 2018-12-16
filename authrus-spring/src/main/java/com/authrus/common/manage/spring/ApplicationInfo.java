package com.authrus.common.manage.spring;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.html.TableCellDrawer;
import com.authrus.common.html.TableDrawer;
import com.authrus.common.html.TableRowDrawer;
import com.authrus.common.memory.MemoryUnit;
import com.authrus.common.time.DateTime;
import com.authrus.common.time.DateTime.Duration;

@ManagedResource(description="Contains application details")
public class ApplicationInfo implements ApplicationAgent {

   private final InetAddress hostAddress;
   private final DateTime startTime;

   public ApplicationInfo() throws Exception {
      this.hostAddress = InetAddress.getLocalHost();
      this.startTime = DateTime.now();
   }   

   @ManagedAttribute(description="Name of host")
   public String getHostName() {     
      return hostAddress.getHostName();
   }   

   @ManagedAttribute(description="Current working directory")
   public String getWorkingDirectory() {
      return System.getProperty("user.dir");
   }

   @ManagedAttribute(description="Owner of the process")
   public String getProcessOwner() {
      return System.getProperty("user.name");
   }

   @ManagedAttribute(description="Time elapsed since start up")
   public String getUpTime() {
      DateTime dateTime = DateTime.now();
      Duration timeElapsed = dateTime.timeDifference(startTime);
      return timeElapsed.toString();
   }

   @ManagedAttribute(description="Time on the host machine")
   public String getHostTime() {
      DateTime currentTime = DateTime.now();
      return currentTime.formatDate("dd/MM/yyyy HH:mm:ss");
   }

   @ManagedAttribute(description="Time on the current host")
   public String getHostTimeZone() {
      return TimeZone.getDefault().getID();
   }

   @ManagedAttribute(description="Percentage or memory used")
   public String getMemoryPercentageUsed() {
      double memoryLimit = Runtime.getRuntime().maxMemory();
      double memoryAllocated = Runtime.getRuntime().totalMemory();
      double memoryFree = Runtime.getRuntime().freeMemory();
      double memoryAvailable = memoryLimit - memoryAllocated;
      double memoryUsed = memoryLimit - (memoryFree + memoryAvailable);
      double percentageUsed = (memoryUsed / memoryLimit) * 100f;

      return Math.round(percentageUsed) + "%";
   }
   
   @ManagedAttribute(description="Total amount of memory available")
   public String getMemoryTotal() {
      long size = Runtime.getRuntime().totalMemory();
      return MemoryUnit.format(size); 
   }  

   @ManagedAttribute(description="Maximum amount of memory available")
   public String getMemoryCapacity() {
      long size = Runtime.getRuntime().maxMemory();
      return MemoryUnit.format(size); 
   }  

   @ManagedOperation(description="Shows how much memory is used")
   public String showMemoryUsage() {      
      List<MemoryPoolMXBean> memoryPools = ManagementFactory.getMemoryPoolMXBeans();
      
      if(!memoryPools.isEmpty()) {
         TableDrawer table = new TableDrawer("name", "size", "used", "free", "chart");
   
         for (MemoryPoolMXBean memoryPool : memoryPools) {
            String name = memoryPool.getName();
            TableRowDrawer row = table.newRow();
            MemoryUsage usage = memoryPool.getUsage();
            double defaultMax = Runtime.getRuntime().maxMemory();
            double explicitMax = usage.getMax();
            double memoryUsed = usage.getUsed();
            double memoryMax = explicitMax > 0 ? explicitMax : defaultMax;
            double memoryFree = memoryMax - memoryUsed;
            long widthUsed = Math.round(700 * (memoryUsed / memoryMax));
            long widthFree = Math.round(700 - widthUsed);
            String formattedMax = MemoryUnit.format(memoryMax);
            String formattedUsed = MemoryUnit.format(memoryUsed);
            String formattedFree = MemoryUnit.format(memoryFree);
            String sizeUsed = String.valueOf(widthUsed);
            String sizeFree = String.valueOf(widthFree);
   
            row.setNormal("name", name);
            row.setNormal("size", formattedMax);
            row.setNormal("used", formattedUsed);
            row.setNormal("free", formattedFree);
            
            TableCellDrawer<TableDrawer> chart = row.setTable("chart", "used", "free");
            TableDrawer chartTable = chart.getValue();
            TableRowDrawer chartRow = chartTable.newRow();
            TableCellDrawer chartUsedCell = chartRow.setEmpty("used");
            TableCellDrawer chartFreeCell = chartRow.setEmpty("free");
            
            chartTable.setBorder(0);
            chartTable.setHeader(false);
            chartUsedCell.setHeight("20");
            chartUsedCell.setColor("#00ff00");
            chartUsedCell.setWidth(sizeUsed);
            chartFreeCell.setHeight("20");
            chartFreeCell.setColor("#ff0000");
            chartFreeCell.setWidth(sizeFree);
         }        
         return table.drawTable();
      }
      return null;
   }

   @ManagedOperation(description="Show system properties")
   public String showSystemProperties() {      
      Properties properties = System.getProperties();
      Set<String> propertyNames = properties.stringPropertyNames();
      
      if(!propertyNames.isEmpty()) {
         TreeSet<String> sortedNames = new TreeSet<String>(propertyNames);
         TableDrawer table = new TableDrawer("property", "value");
   
         for (String propertyName : sortedNames) {
            TableRowDrawer row = table.newRow();
            String propertyValue = properties.getProperty(propertyName);
            
            row.setNormal("property", propertyName);
            row.setNormal("value", propertyValue);
         }
         return table.drawTable();
      }
      return null;
   }
}
