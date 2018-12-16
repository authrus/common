package com.authrus.common.swing.ui;

import static org.junit.Assert.*;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.junit.Test;
import org.simpleframework.xml.core.Persister;

import com.authrus.common.swing.ui.annotation.ComponentOf;
import com.authrus.common.swing.ui.annotation.PanelCreate;
import com.authrus.common.swing.ui.table.TableModel;
import com.authrus.common.swing.ui.table.TableUpdater;

public class TableTest {

   private static final String SOURCE = "<panel id='test' boundary='fatpad'>\n" + "  <scroll>\n" + "    <table id='closeTable' height='200' width='300' refreshFrequency='200'>\n" + "      <type>"
         + com.authrus.common.swing.ui.TableTest.ExampleRow.class.getName() + "</type>\n" + "      <text property='A' />\n" + "      <text property='B' ignoreNull='true' />\n"
         + "      <text property='C' ignoreNull='true' />\n" + "      <text property='D' />\n" + "    </table>\n" + "  </scroll>\n" + "</panel>";

   private static class ExampleRow {

      private String a;
      private String b;
      private String c;
      private String d;

      public ExampleRow(String a, String b, String c, String d) {
         this.a = a;
         this.b = b;
         this.c = c;
         this.d = d;
      }

      public String getA() {
         return a;
      }

      public String getB() {
         return b;
      }

      public String getC() {
         return c;
      }

      public String getD() {
         return d;
      }
   }

   private static class TableController implements Controller {

      private TableUpdater tableUpdater;
      private TableModel tableModel;
      private JTable table;

      public TableModel getTableModel() {
         return tableModel;
      }

      public TableUpdater getTableUpdater() {
         return tableUpdater;
      }

      public JTable getTable() {
         return table;
      }

      @PanelCreate("test")
      public void onCreate(@ComponentOf("closeTable") TableUpdater tableUpdater, @ComponentOf("closeTable") TableModel tableModel, @ComponentOf("closeTable") JTable table) {
         this.tableUpdater = tableUpdater;
         this.tableModel = tableModel;
         this.table = table;
      }

      @Override
      public Object resolve(Context context) {
         return this;
      }
   }

   @Test
   public void testUpdate() throws Exception {
      Persister persister = new Persister();
      Panel panel = persister.read(Panel.class, SOURCE);
      TableController tableController = new TableController();
      Context context = new WindowContext();
      JPanel swingPanel = panel.build(tableController, context);
      TableUpdater tableUpdater = tableController.getTableUpdater();
      TableModel tableModel = tableController.getTableModel();
      JTable table = tableController.getTable();

      assertNotNull(swingPanel);
      assertNotNull(tableUpdater);
      assertNotNull(tableModel);
      assertNotNull(table);

      ExampleRow row1 = new ExampleRow("a-key", "b1", "c1", "d1");
      ExampleRow row2 = new ExampleRow("a-key", null, "c2", null);
      ExampleRow row3 = new ExampleRow("a-key", "b3", "c3", "d3");
      ExampleRow row4 = new ExampleRow("a-key", "b4", null, "d4");

      tableUpdater.update(row1);

      assertEquals(tableModel.getValueAt(0, 0), "a-key");
      assertEquals(tableModel.getValueAt(0, 1), "b1");
      assertEquals(tableModel.getValueAt(0, 2), "c1");
      assertEquals(tableModel.getValueAt(0, 3), "d1");

      tableUpdater.update(row2);

      assertEquals(tableModel.getValueAt(0, 0), "a-key");
      assertEquals(tableModel.getValueAt(0, 1), "b1"); // ignoreNull=true
      assertEquals(tableModel.getValueAt(0, 2), "c2");
      assertEquals(tableModel.getValueAt(0, 3), ""); // ignoreNull=false

      tableUpdater.update(row3);

      assertEquals(tableModel.getValueAt(0, 0), "a-key");
      assertEquals(tableModel.getValueAt(0, 1), "b3");
      assertEquals(tableModel.getValueAt(0, 2), "c3");
      assertEquals(tableModel.getValueAt(0, 3), "d3");

      tableUpdater.update(row4);

      assertEquals(tableModel.getValueAt(0, 0), "a-key");
      assertEquals(tableModel.getValueAt(0, 1), "b4");
      assertEquals(tableModel.getValueAt(0, 2), "c3"); // ignoreNull=true
      assertEquals(tableModel.getValueAt(0, 3), "d4");

   }

   public static void main() {

   }

}
