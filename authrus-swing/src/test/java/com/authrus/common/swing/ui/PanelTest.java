package com.authrus.common.swing.ui;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.junit.Test;

import com.authrus.common.swing.ui.Context;
import com.authrus.common.swing.ui.Controller;
import com.authrus.common.swing.ui.Panel;
import com.authrus.common.swing.ui.WindowContext;
import com.authrus.common.swing.ui.annotation.ComponentOf;
import com.authrus.common.swing.ui.annotation.PanelCreate;
import com.authrus.common.swing.ui.annotation.ValueOf;

public class PanelTest {

   public static class Example {

      private Map<String, Object> values;

      public Example(Map<String, Object> values) {
         this.values = values;
      }

      @PanelCreate("example1")
      public void onCreate() {
      }

      @PanelCreate("example2")
      public void onCreate(@ComponentOf("text2") JTextField field, @ComponentOf("label1") JLabel label) {
         values.put("text2", field);
         values.put("label1", label);
      }

      @PanelCreate("example3")
      public void onCreate(@ValueOf("label1") String value) {
         values.put("label1.value", value);
      }

      @PanelCreate("example4")
      public void onCreate(@ValueOf("text3") String text3Text, @ValueOf("text1") String text1Text, @ComponentOf("text3") JTextField text3) {
         values.put("text3", text3);
         values.put("text3.value", text3Text);
         values.put("text1.value", text1Text);
      }

   }

   private static class ExampleController implements Controller {

      private final Example example;

      public ExampleController(Example example) {
         this.example = example;
      }

      public Object resolve(Context context) {
         return example;
      }
   }

   @Test
   public void testPanel() {
      Panel panel = new Panel();
      panel.id = "example2";
      Context context = new WindowContext();
      Map<String, Object> values = new HashMap<String, Object>();
      Example example = new Example(values);
      Controller controller = new ExampleController(example);
      JTextField t1 = new JTextField("Text Example 1");
      JTextField t2 = new JTextField("Text Example 2");
      JTextField t3 = new JTextField("Text Example 3");
      JTextField t4 = new JTextField("Text Example 4");
      JLabel l1 = new JLabel("Label Example 1");

      context.add("text1", t1);
      context.add("text2", t2);
      context.add("text3", t3);
      context.add("text4", t4);
      context.add("label1", l1);

      panel.build(controller, context);

      assertEquals(l1, values.get("label1"));
      assertEquals(t2, values.get("text2"));

      panel = new Panel();
      panel.id = "example3";

      panel.build(controller, context);

      assertEquals("Label Example 1", values.get("label1.value"));

      panel = new Panel();
      panel.id = "example4";

      panel.build(controller, context);

      assertEquals(t3, values.get("text3"));
      assertEquals("Text Example 1", values.get("text1.value"));
      assertEquals("Text Example 3", values.get("text3.value"));
   }
}
