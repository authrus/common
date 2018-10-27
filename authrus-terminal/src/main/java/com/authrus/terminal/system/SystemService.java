package com.authrus.terminal.system;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

@Component
public class SystemService {

   private final OperatingSystemMXBean os;
   private final FileSystem fs;
   
   public SystemService() {
      this.os = ManagementFactory.getOperatingSystemMXBean();
      this.fs = FileSystems.getDefault();
   }
   
   @SneakyThrows
   public ActivityResult getActivity()  {
      double usage = os.getSystemLoadAverage();
      int cores = os.getAvailableProcessors();
      
      return new ActivityResult(usage, cores);
   }   

   @SneakyThrows
   public DiskResult getDisk() {
      Iterable<FileStore> stores = fs.getFileStores();
      Iterator<FileStore> iterator = stores.iterator();
      
      if(iterator.hasNext()) {
         List<DiskPartition> partitions = new ArrayList<DiskPartition>();
         Set<String> descriptions = new HashSet<String>();
         
         while(iterator.hasNext()) {
            FileStore store = iterator.next();
            String name = store.name();
            String description = store.toString();
            long total = store.getTotalSpace();
            long usable = store.getUsableSpace();
            long free = store.getUnallocatedSpace();
            
            if(total > 0) {
               DiskPartition partition = new DiskPartition(name, description, total, usable, free);
               
               if(descriptions.add(description)) {
                  partitions.add(partition);
               }
            }
         }
         return new DiskResult(partitions);
      }
      return new DiskResult(Collections.EMPTY_LIST);
   }

}
