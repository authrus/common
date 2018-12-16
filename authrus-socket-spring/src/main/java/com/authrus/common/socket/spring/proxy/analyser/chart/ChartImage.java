package com.authrus.common.socket.spring.proxy.analyser.chart;

import static org.simpleframework.http.Protocol.CONTENT_TYPE;
import static org.simpleframework.http.Protocol.DATE;

import java.io.OutputStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public class ChartImage {

   private final ChartSeriesPlotter plotter;
   private final String series;
   private final String type;

   public ChartImage(ChartSeriesPlotter plotter, String series, String type) {
      this.plotter = plotter;
      this.series = series;
      this.type = type;
   }

   public void draw(Request request, Response response) throws Exception {
      OutputStream output = response.getOutputStream();
      long date = System.currentTimeMillis();
      byte[] data = plotter.getPlot(series);

      response.setDate(DATE, date);
      response.setValue(CONTENT_TYPE, type);
      response.setContentLength(data.length);
      output.write(data);
      output.close();
   }
}
