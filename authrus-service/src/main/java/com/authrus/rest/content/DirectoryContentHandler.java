package com.authrus.rest.content;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.simpleframework.http.Status.OK;

import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@AllArgsConstructor
public class DirectoryContentHandler implements ContentHandler {

   private final ContentTypeResolver resolver;
   private final ObjectMapper mapper;
   private final FileContent record;
   
   @Override
   @SneakyThrows
   public void handle(Request request, Response response) {
      try {
         File file = record.getFile();
         File[] files = file.listFiles();
         OutputStream stream = response.getOutputStream();
         List<DirectoryContent> list = Arrays.asList(files)
               .stream()
               .filter(Objects::nonNull)
               .map(entry -> {
                  String name = entry.getName();
                  String type = resolver.resolveType(name);
                  long updateTime = entry.lastModified();
                  long length = entry.length();
                  boolean directory = entry.isDirectory();
                  
                  return DirectoryContent.builder()
                        .name(name)
                        .directory(directory)
                        .updateTime(updateTime)
                        .length(length)
                        .type(type)
                        .build();
               })
               .collect(Collectors.toList());
         
         response.setStatus(OK);
         response.setContentType(APPLICATION_JSON);
         mapper.writeValue(stream, list);
         stream.close();
      }catch(Exception e){
         log.info("Could not complete request", e);
      }
   }
}
