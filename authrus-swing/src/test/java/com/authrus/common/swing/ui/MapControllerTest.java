package com.authrus.common.swing.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.authrus.common.swing.ui.Context;
import com.authrus.common.swing.ui.Controller;
import com.authrus.common.swing.ui.FormContext;
import com.authrus.common.swing.ui.MapController;
import com.authrus.common.swing.ui.Panel;
import com.authrus.common.swing.ui.WindowContext;
import com.authrus.common.swing.ui.annotation.PanelCreate;

public class MapControllerTest {

   public static class ActionMain {
      public boolean onCreate;

      @PanelCreate("id1")
      public void onCreate() {
         onCreate = true;
      }
   }

   public static class ActionA {
      public boolean onCreate;

      @PanelCreate("id1")
      public void onCreate() {
         onCreate = true;
      }
   }

   public static class ActionB {
      public boolean onCreate;

      @PanelCreate("id1")
      public void onCreate() {
         onCreate = true;
      }
   }

   public static class ActionC {
      public boolean onCreate;

      @PanelCreate("id1")
      public void onCreate() {
         onCreate = true;
      }
   }

   @Test
   public void testPanel() {
      Map<String, Object> actions = new HashMap<String, Object>();
      ActionA a = new ActionA();
      ActionB b = new ActionB();
      ActionC c = new ActionC();
      ActionMain main = new ActionMain();

      actions.put("A", a);
      actions.put("B", b);
      actions.put("C", c);

      Controller controller = new MapController(actions, main);
      Context context = new WindowContext();

      assertEquals(controller.resolve(context), main);
      assertEquals(controller.resolve(new FormContext(context, "A")), a);
      assertEquals(controller.resolve(new FormContext(context, "B")), b);
      assertEquals(controller.resolve(new FormContext(context, "C")), c);

      assertFalse(main.onCreate);
      assertFalse(a.onCreate);
      assertFalse(b.onCreate);
      assertFalse(c.onCreate);

      Panel panel = new Panel();
      panel.id = "id1";

      panel.build(controller, context);
      panel.build(controller, new FormContext(context, "A"));
      panel.build(controller, new FormContext(context, "B"));
      panel.build(controller, new FormContext(context, "C"));

      assertTrue(main.onCreate);
      assertTrue(a.onCreate);
      assertTrue(b.onCreate);
      assertTrue(c.onCreate);
   }
}
