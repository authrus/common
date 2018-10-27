package com.authrus.rest.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DirectoryContent {

   private final String type;
   private final String name;
   private final long length;
   private final long updateTime;
   private final boolean directory;
}
