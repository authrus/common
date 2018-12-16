package com.authrus.common.socket.spring.proxy.analyser.chart;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(description = "Contains ability to render charts")
public class ChartAnalyser {

   private final Map<String, ChartImageProvider> providers;
   private final String host;
   private final int port;

   public ChartAnalyser(Map<String, ChartImageProvider> providers, int port) throws Exception {
      this.host = InetAddress.getLocalHost().getCanonicalHostName();
      this.providers = providers;
      this.port = port;
   }

   @ManagedOperation(description = "Show all charts")
   public String showCharts() {
      StringBuilder builder = new StringBuilder();

      if (!providers.isEmpty()) {
         Set<String> types = providers.keySet();

         for (String type : types) {
            String text = showCharts(type);
            builder.append(text);
         }
      }
      return builder.toString();
   }

   @ManagedOperation(description = "Show charts of type")
   @ManagedOperationParameters({ @ManagedOperationParameter(name = "type", description = "Type of charts to show") })
   public String showCharts(String type) {
      StringBuilder builder = new StringBuilder();

      if (providers.containsKey(type)) {
         ChartImageProvider provider = providers.get(type);
         Set<String> charts = provider.getCharts();

         for (String chart : charts) {
            String text = showCharts(type, chart);
            builder.append(text);
         }
      }
      return builder.toString();
   }

   @ManagedOperation(description = "Show specific chart of type")
   @ManagedOperationParameters({ @ManagedOperationParameter(name = "type", description = "Type of charts to show"),
         @ManagedOperationParameter(name = "chart", description = "Name of the chart to show") })
   public String showCharts(String type, String chart) {
      StringBuilder builder = new StringBuilder();

      if (providers.containsKey(type)) {
         ChartImageProvider provider = providers.get(type);
         Set<String> connections = provider.getSeries(chart);

         for (String connection : connections) {
            long time = System.currentTimeMillis();

            builder.append("<center>");
            builder.append("<img src='http://");
            builder.append(host);
            builder.append(":");
            builder.append(port);
            builder.append("/");
            builder.append(type);
            builder.append("/");
            builder.append(chart);
            builder.append("/");
            builder.append(connection);
            builder.append("?time=");
            builder.append(time);
            builder.append("' style='border: 1px solid black'/>");
            builder.append("<hr>");
            builder.append("</center>");
         }
      }
      return builder.toString();
   }

   public ChartImage getChart(String type, String chart, String connection) {
      ChartImageProvider provider = providers.get(type);

      if (provider != null) {
         return provider.getImage(chart, connection);
      }
      return null;
   }
}
