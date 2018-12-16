package com.authrus.tuple.query;

import java.util.Map;
import java.util.Set;

import com.authrus.io.DataWriter;

public class QueryWriter {
   
   public QueryWriter() {
      super();
   }

   public void writeQuery(DataWriter writer, Query query) throws Exception {
      Map<String, String> predicates = query.getPredicates();
      Set<String> patterns = predicates.keySet();
      Origin origin = query.getOrigin();
      int count = predicates.size();

      writeOrigin(writer, origin);
      writer.writeInt(count);

      for (String pattern : patterns) {
         String predicate = predicates.get(pattern);

         writer.writeString(pattern);
         writer.writeString(predicate);
      }
   }

   private void writeOrigin(DataWriter writer, Origin origin) throws Exception {
      String name = origin.getName();
      String host = origin.getHost();
      int port = origin.getPort();

      writer.writeString(name);

      if (host == null) {
         writer.writeBoolean(false);
      } else {
         writer.writeBoolean(true);
         writer.writeString(host);
         writer.writeInt(port);
      }
   }
}
