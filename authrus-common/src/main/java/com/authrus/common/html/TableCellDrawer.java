package com.authrus.common.html;

import java.io.PrintWriter;

public class TableCellDrawer<T> {

   private String tag;
   private String width;
   private String height;
   private String color;
   private T value;
   private boolean wrap;

   public TableCellDrawer(T value) {
      this(value, null);
   }

   public TableCellDrawer(T value, String tag) {
      this(value, tag, true);
   }
   
   public TableCellDrawer(T value, String tag, boolean wrap) {
      this.value = value;
      this.wrap = wrap;
      this.tag = tag;
   }

   public void drawCell(PrintWriter writer) {
      CellFormatter formatter = createFormatter(value);

      if (tag != null) {
         writer.print("<");
         writer.print(tag);
         writer.print(">");
      }
      formatter.drawCell(writer, value);

      if (tag != null) {
         writer.print("</");
         writer.print(tag);
         writer.print(">");
      }
   }
   
   public T getValue() {
      return value;
   }
   
   public void setValue(T value) {
      this.value = value;
   }
   
   public boolean isWrap() {
      return wrap;
   }
   
   public void setWrap(boolean wrap) {
      this.wrap = wrap;
   }
   
   public String getWidth() {
      return width;
   }
   
   public void setWidth(String width) {
      this.width = width;
   }
   
   public String getHeight() {
      return height;
   }
   
   public void setHeight(String height) {
      this.height = height;
   }   
   
   public String getColor() {
      return color;
   }
   
   public void setColor(String color) {
      this.color = color;
   }   

   private CellFormatter createFormatter(Object value) {
      if (value == null) {
         return new CommentFormatter();
      }
      if (value instanceof Throwable) {
         return new ThrowableFormatter();
      }
      if (value instanceof TableDrawer) {
         return new TableFormatter();
      }
      return new ObjectFormatter();
   }

   private static interface CellFormatter<T> {
      void drawCell(PrintWriter writer, T value);
   }
   
   private static class TableFormatter implements CellFormatter<TableDrawer> {

      public void drawCell(PrintWriter writer, TableDrawer drawer) {
         writer.print(drawer);
      }
   }

   private static class ThrowableFormatter implements CellFormatter<Throwable> {

      public void drawCell(PrintWriter writer, Throwable value) {
         value.printStackTrace(writer);
      }
   }

   private static class ObjectFormatter implements CellFormatter<Object> {

      public void drawCell(PrintWriter writer, Object value) {
         String text = String.valueOf(value);
         writer.print(text);
      }
   }

   private static class CommentFormatter implements CellFormatter<Object> {

      public void drawCell(PrintWriter writer, Object value) {
         String text = String.valueOf(value);

         writer.print("<!--");
         writer.print(text);
         writer.print("-->");
      }
   }
}
