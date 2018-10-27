package com.authrus.rest.content;

import java.io.File;
import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileContent {

   private final URL resource;
   private final File file;
   private final String path;
   private final boolean directory;
}
