package com.authrus.common.socket.spring.proxy.analyser.chart;

import static org.simpleframework.http.Protocol.NO_CACHE;
import static org.simpleframework.http.Protocol.PRAGMA;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChartContainer implements Container {

   private static final Logger LOG = LoggerFactory.getLogger(ChartContainer.class);

   private final ChartAnalyser analyser;

   public ChartContainer(ChartAnalyser analyser) {
      this.analyser = analyser;
   }

   @Override
   public void handle(Request req, Response resp) {
      Path path = req.getPath();
      String[] parts = path.getSegments();
      ChartImage resource = analyser.getChart(parts[0], parts[1], parts[2]);

      try {
         resp.setValue(PRAGMA, NO_CACHE);
         resource.draw(req, resp);
      } catch (Throwable cause) {
         LOG.info("Internal server error", cause);
      }
   }
   
}
