package com.authrus.common.swing.ui.graph;

import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.core.Commit;

import com.authrus.common.chart.Chart;
import com.authrus.common.chart.ChartType;
import com.authrus.common.chart.Series;
import com.authrus.common.chart.plot.Plot;
import com.authrus.common.chart.plot.PlotSeries;
import com.authrus.common.chart.reflect.PropertyChart;
import com.authrus.common.swing.ui.Context;
import com.authrus.common.swing.ui.Controller;
import com.authrus.common.swing.ui.Widget;
import com.authrus.common.swing.ui.WidgetException;

public class Graph extends Widget<JLabel> {

   @ElementList(entry="plot", inline=true)
   private List<PlotBuilder> builders;

   @Transient
   private List<Plot> plots;

   @Transient
   private GraphModel model;

   @Attribute(required=false)
   private int pad;

   @Attribute(required=false)
   private String boundary;

   @Attribute(required = false)
   private String title;

   @Attribute
   private String parent;

   @Attribute
   private int capacity;

   @Attribute
   private String x;

   @Attribute
   private String y;

   @Element
   private Class type;

   public Graph(@Attribute(name="capacity") int capacity) {
      this.model = new GraphModel(capacity);
      this.plots = new LinkedList<Plot>();
      this.title = new String();
   }

   @Commit
   public void commit() {
      for (PlotBuilder builder : builders) {
         Plot plot = builder.build(model, type);
         plots.add(plot);
      }
   }

   @Override
   public JLabel build(Controller controller, Context context, Dimension size) {
      JComponent holder = context.get(parent);

      if (holder == null) {
         throw new WidgetException("Parent panel %s does not exist", parent);
      }
      return build(controller, context, holder);
   }

   public JLabel build(Controller controller, Context context, Component parent) {
      PlotSeries series = new PlotSeries(plots, title, x, y);
      PlotDrawer plotDrawer = new PlotDrawer(series, parent, pad);
      GraphPanel graphPanel = new GraphPanel(model);
      GraphManager graphManager = new GraphManager(graphPanel, plotDrawer, boundary);

      if (id != null) {
         context.add(id, graphPanel);
      }
      parent.addComponentListener(graphManager);
      graphManager.start();
      return graphPanel;
   }

   private static class PlotBuilder {

      @Attribute(required=false)
      private ChartType chartType;

      @Attribute
      private String name;

      @Attribute
      private String x;

      @Attribute
      private String y;

      public Plot build(Series series, Class type) {
         if (chartType != null) {
            return build(series, type, chartType);
         }
         return build(series, type, ChartType.LINE);
      }

      public Plot build(Series series, Class type, ChartType chartType) {
         Chart chart = new PropertyChart(series, null, x, y, type, chartType);

         if (series == null) {
            throw new WidgetException("Series required for plot");
         }
         return new Plot(chart, name);
      }
   }
}
