package com.authrus.common.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TableDrawer {

   private final List<TableRowDrawer> rows;
   private final AtomicBoolean header;
   private final AtomicInteger border;
   private final String[] headings;

   public TableDrawer(String... headings) {
      this.rows = new ArrayList<TableRowDrawer>();
      this.header = new AtomicBoolean(true);
      this.border = new AtomicInteger(1);
      this.headings = headings;
   }

   public String drawTable() {
      StringWriter buffer = new StringWriter();
      PrintWriter writer = new PrintWriter(buffer);

      if (!rows.isEmpty()) {         
         int size = border.get();
         
         writer.print("<table border='");
         writer.print(size);
         writer.println("'>");

         if(header.get()) {
            for (String heading : headings) {
               writer.print("<th>");
               writer.print(heading);
               writer.println("</th>");
            }
         }
         for (TableRowDrawer row : rows) {
            row.drawRow(writer);
         }
         writer.print("</table>");
         writer.close();
      }
      return buffer.toString();
   }
   
   public int getBorder() {
      return border.get();
   }
   
   public void setBorder(int size) {
      border.set(size);
   }
   
   public boolean isHeader() {
      return header.get();
   }

   public void setHeader(boolean enable) {
      header.set(enable);
   }

   public TableRowDrawer newRow(Object... values) {
      TableRowDrawer row = new TableRowDrawer(headings);

      for (int i = 0; i < values.length; i++) {
         row.setNormal(i, values[i]);
      }
      rows.add(row);
      return row;
   }
   
   @Override
   public String toString() {
      return drawTable();
   }
}
