package com.authrus.common.html;

import java.io.PrintWriter;

public class TableRowDrawer {

   private TableCellDrawer[] cells;
   private String[] headings;
   private String color;

   public TableRowDrawer(String[] headings) {
      this.cells = new TableCellDrawer[headings.length];
      this.headings = headings;
   }

   public void drawRow(PrintWriter writer) {
      if (color == null) {
         writer.println("<tr>");
      } else {
         writer.print("<tr bgcolor='");
         writer.print(color);
         writer.println("'>");
      }
      for (TableCellDrawer cell : cells) {
         if(cell == null) {
            writer.println("<td></td>");
         } else {
            String width = cell.getWidth();
            String height = cell.getHeight();
            String color = cell.getColor();
            
            writer.print("<td");
            
            if(width != null) {
               writer.print(" width='");
               writer.print(width);
               writer.print("'");
            } 
            if(height != null) {
               writer.print(" height='");
               writer.print(height);
               writer.print("'");
            }
            if(color != null) {
               writer.print(" bgcolor='");
               writer.print(color);
               writer.print("'");
            }
            if(cell.isWrap()) {
               writer.print(">");
            } else {
               writer.print(" nowrap>");
            }
            cell.drawCell(writer);         
            writer.println("</td>");
         }
      }
      writer.println("</tr>");
   }
   
   public String getColor() {
      return color;
   }

   public void setColor(String code) {
      color = code;
   }

   public int getIndex(String heading) {
      for (int i = 0; i < headings.length; i++) {
         if (headings[i].equals(heading)) {
            return i;
         }
      }
      return -1;
   }
   
   public TableCellDrawer<TableDrawer> setTable(String heading, String... headings) {
      int index = getIndex(heading);

      if (index == -1) {
         throw new IllegalArgumentException("Heading '" + heading + "' does not exist");
      }
      return setTable(index, headings);
   }

   public TableCellDrawer<TableDrawer> setTable(int index, String... headings) {      
      TableDrawer drawer = new TableDrawer(headings);
      
      if(headings.length == 0) {
         throw new IllegalArgumentException("Column table at index '" + index + "' has no headings");
      }
      return setNormal(index, drawer);      
   }

   public <T> TableCellDrawer<T> setNormal(String heading, T value) {
      int index = getIndex(heading);

      if (index == -1) {
         throw new IllegalArgumentException("Heading '" + heading + "' does not exist");
      }
      return setNormal(index, value);
   }

   public <T> TableCellDrawer<T> setNormal(int index, T value) {
      TableCellDrawer<T> drawer = new TableCellDrawer<T>(value);      
      
      if (index < 0 || index >= cells.length) {
         throw new IllegalArgumentException("Column index " + index + " is out of bounds");
      }
      cells[index] = drawer;       
      return drawer;
   }

   public <T> TableCellDrawer<T> setBold(String heading, T value) {
      int index = getIndex(heading);

      if (index == -1) {
         throw new IllegalArgumentException("Heading '" + heading + "' does not exist");
      }
      return setBold(index, value);
   }

   public <T> TableCellDrawer<T> setBold(int index, T value) {
      TableCellDrawer<T> drawer = new TableCellDrawer<T>(value, "b");
      
      if (index < 0 || index >= cells.length) {
         throw new IllegalArgumentException("Column index " + index + " is out of bounds");
      }
      cells[index] = drawer;
      return drawer;
   }

   public <T> TableCellDrawer<T> setCode(String heading, T value) {
      int index = getIndex(heading);

      if (index == -1) {
         throw new IllegalArgumentException("Heading '" + heading + "' does not exist");
      }
      return setCode(index, value);
   }

   public <T> TableCellDrawer<T> setCode(int index, T value) {
      TableCellDrawer<T> drawer = new TableCellDrawer<T>(value, "pre");
      
      if (index < 0 || index >= cells.length) {
         throw new IllegalArgumentException("Column index " + index + " is out of bounds");
      }
      cells[index] = drawer;
      return drawer;
   }
   
   public <T> TableCellDrawer<T> setEmpty(String heading) {
      int index = getIndex(heading);

      if (index == -1) {
         throw new IllegalArgumentException("Heading '" + heading + "' does not exist");
      }
      return setEmpty(index);
   }

   public <T> TableCellDrawer<T> setEmpty(int index) {
      TableCellDrawer<T> drawer = new TableCellDrawer<T>(null);      
      
      if (index < 0 || index >= cells.length) {
         throw new IllegalArgumentException("Column index " + index + " is out of bounds");
      }
      cells[index] = drawer;       
      return drawer;
   }   
}
