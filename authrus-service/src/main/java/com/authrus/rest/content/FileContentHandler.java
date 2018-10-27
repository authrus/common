package com.authrus.rest.content;

import static org.simpleframework.http.Status.OK;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.tika.io.IOUtils;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

@Slf4j
@AllArgsConstructor
public class FileContentHandler implements ContentHandler {

   private final FileContent record;
   private final String type;
   
   @SneakyThrows
   @Override
   public void handle(Request request, Response response) {
      try {
         URL resource = record.getResource();
         InputStream source = resource.openStream();
         OutputStream output = response.getOutputStream();
         
         response.setStatus(OK);
         response.setContentType(type);
         IOUtils.copy(source, output);
         output.flush();
         output.close();
         source.close();
      }catch(Exception e) {
         log.info("Could not complete request", e);
      } finally {
         response.close();
      }
   }

}
