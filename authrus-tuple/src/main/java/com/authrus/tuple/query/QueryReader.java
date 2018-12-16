package com.authrus.tuple.query;

import java.util.LinkedHashMap;
import java.util.Map;

import com.authrus.io.DataReader;

public class QueryReader {

   public QueryReader() {
      super();
   }

   public Query readQuery(DataReader reader) throws Exception {
      Origin origin = readOrigin(reader);
      int count = reader.readInt();

      if (count > 0) {
         Map<String, String> predicates = new LinkedHashMap<String, String>();

         for (int i = 0; i < count; i++) {
            String type = reader.readString();
            String predicate = reader.readString();

            predicates.put(type, predicate);
         }
         return new Query(origin, predicates);
      }
      return new Query(origin);
   }

   private Origin readOrigin(DataReader reader) throws Exception {
      String name = reader.readString();

      if (reader.readBoolean()) {
         String host = reader.readString();
         int port = reader.readInt();

         return new Origin(name, host, port);
      }
      return new Origin(name);
   }
}
