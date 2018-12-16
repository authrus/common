package com.authrus.common.swing.ui.table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableManager extends Thread {

   private static final Logger LOG = LoggerFactory.getLogger(TableManager.class);

   private final TableUpdater tableUpdater;
   private final long refresh;

   public TableManager(TableUpdater tableUpdater, long refresh) {
      this.tableUpdater = tableUpdater;
      this.refresh = refresh;
   }

   public void run() {
      while (true) {
         try {
            tableUpdater.refresh();
            Thread.sleep(refresh);
         } catch (Exception e) {
            LOG.info("Could not draw table", e);
            Thread.yield();
         }
      }
   }
}
