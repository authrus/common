package com.authrus.common.socket.spring.proxy.analyser;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.html.TableDrawer;
import com.authrus.common.html.TableRowDrawer;
import com.authrus.common.socket.proxy.analyser.Packet;
import com.authrus.common.socket.proxy.analyser.PacketAnalyser;
import com.authrus.common.time.DateTime;
import com.authrus.common.time.Time;

@ManagedResource(description="Traces all TCP traffic")
public class TraceAnalyser implements PacketAnalyser {
   
   private final BlockingQueue<TraceRecord> records;
   private final AtomicBoolean enabled;
   private final int capacity;
   
   public TraceAnalyser(int capacity) {
      this.records = new LinkedBlockingQueue<TraceRecord>();
      this.enabled = new AtomicBoolean();
      this.capacity = capacity;
   }   
   
   @ManagedAttribute(description="Determine if tracing is enabled")
   public boolean isEnabled() {
	   return enabled.get();
   }
   
   @ManagedOperation(description="Enabled or disable tracing")
   @ManagedOperationParameters({
	   @ManagedOperationParameter(name="flag", description="Enable or disable flag")
   })   
   public void setEnabled(boolean flag) {
	   enabled.set(flag);
   }
   
   @ManagedOperation(description="Show captured packets")
   public String showRecords() {
      TableDrawer drawer = new TableDrawer("index", "time", "connection", "packet");
      
      if(!records.isEmpty()) {
         int index = 0;
         
         for(TraceRecord record : records) {
            TableRowDrawer rowDrawer = drawer.newRow();
            DateTime timeStamp = record.getTimeStamp();
            String connection = record.getConnection();
            String packet = record.getContent();

            rowDrawer.setNormal("index", index++);
            rowDrawer.setNormal("time", timeStamp).setWrap(false);
            rowDrawer.setNormal("connection", connection).setWrap(false);       
            rowDrawer.setCode("packet", packet);
         }
      }
      return drawer.drawTable();
   }

   @Override
   public void analyse(Time startTime, Time endTime, Packet packetData) {
	   int length = packetData.length();
      boolean trace = enabled.get();
      
      if(length > 0 && trace) {
         TraceRecord record = new TraceRecord(packetData);
         
         if(records.offer(record)) {
            int size = records.size();
            
            if(size > capacity) {
               records.poll();
            }
         }      
      }
   }
   
   @ManagedOperation(description="Clear captured packets")
   public void clear() {
      records.clear();
   }

   private class TraceRecord {
      
      private final DateTime timeStamp; 
      private final String connection;
      private final byte[] data;
      
      public TraceRecord(Packet packetData) {
         this.connection = packetData.connection();
         this.data = packetData.extract();
         this.timeStamp = DateTime.now();    
      }      
      
      public String getContent() {
         try {
            return new String(data, "UTF-8");
         } catch(Exception e) {
            throw new IllegalStateException("Could not decode packet", e);
         }
      }
      
      public DateTime getTimeStamp() {
         return timeStamp;
      }     
      
      public String getConnection() {
         return connection;
      }
      
      public byte[] getData() {
         return data;
      }
   }
}
