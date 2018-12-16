package com.authrus.tuple.spring.grid;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.common.html.TableDrawer;
import com.authrus.common.html.TableRowDrawer;
import com.authrus.predicate.Predicate;
import com.authrus.predicate.PredicateParser;
import com.authrus.tuple.frame.Session;
import com.authrus.tuple.grid.Cell;
import com.authrus.tuple.grid.Column;
import com.authrus.tuple.grid.DeltaMerge;
import com.authrus.tuple.grid.Row;
import com.authrus.tuple.grid.Schema;
import com.authrus.tuple.grid.Version;
import com.authrus.tuple.grid.record.DeltaRecord;

@ManagedResource(description="Search through delta messages")
public class DeltaSearcher {
   
   private final Cache<Session, DeltaIndex> indexes;
   private final int limit;

   public DeltaSearcher() {
      this(2000000);
   }
   
   public DeltaSearcher(int limit) {
      this.indexes = new LeastRecentlyUsedCache<Session, DeltaIndex>();
      this.limit = limit;
   } 
   
   @ManagedOperation(description="Search for messages by predicate")
   @ManagedOperationParameters({     
      @ManagedOperationParameter(name="expression", description="Predicate expression to use")
   })   
   public String searchByPredicate(String expression) {
      Predicate predicate = new PredicateParser(expression);
      StringBuilder builder = new StringBuilder();
      
      if(!indexes.isEmpty()) {
         Set<Session> sessions = indexes.keySet();
         
         for(Session session : sessions) {
            DeltaIndex index = indexes.fetch(session);
            List<DeltaRecord> records = index.list();
            
            for(DeltaRecord record : records) {
               DeltaMerge merge = record.getMerge();
               Row row = merge.getCurrent();
               
               if(predicate.accept(row)) {
                  int length = builder.length();
                  
                  if(length < limit) {
                     String description = searchDescription(record);
                     String result = searchResult(record);
                     
                     builder.append(description);
                     builder.append(result);
                     builder.append("<br>");   
                     builder.append("<hr>");
                  }
               }
            }
         }
      }
      return builder.toString();
   }
   
   @ManagedOperation(description="Search for messages by key")
   @ManagedOperationParameters({   
      @ManagedOperationParameter(name="term", description="Key to find record with")
   })   
   public String searchByKey(String term) {
      StringBuilder builder = new StringBuilder();
      
      if(!indexes.isEmpty()) {
         Set<Session> sessions = indexes.keySet();
         
         for(Session session : sessions) {
            DeltaIndex index = indexes.fetch(session);
            List<DeltaRecord> records = index.list();
            
            for(DeltaRecord record : records) {
               DeltaMerge merge = record.getMerge();
               Row row = merge.getCurrent();
               String key = row.getKey();
               
               if(term.equals(key)) {
                  int length = builder.length();
                  
                  if(length < limit) {
                     String description = searchDescription(record);
                     String result = searchResult(record);
                     
                     builder.append(description);
                     builder.append(result);
                     builder.append("<hr>");
                     builder.append("<br>");
                  }
               }
            }
         }
      }
      return builder.toString();
   }   
   
   @ManagedOperation(description="Search for messages by origin")
   @ManagedOperationParameters({   
      @ManagedOperationParameter(name="term", description="Origin name for the record")
   })   
   public String searchByOrigin(String term) {
      StringBuilder builder = new StringBuilder();
      
      if(!indexes.isEmpty()) {
         Set<Session> sessions = indexes.keySet();
         
         for(Session session : sessions) {
            DeltaIndex index = indexes.fetch(session);
            String token = session.toString();
            
            if(token.contains(term)) {
               List<DeltaRecord> records = index.list();
            
               for(DeltaRecord record : records) {
                  int length = builder.length();
                  
                  if(length < limit) {
                     String description = searchDescription(record);
                     String result = searchResult(record);
                     
                     builder.append(description);
                     builder.append(result);
                     builder.append("<hr>");
                     builder.append("<br>");  
                  }
               }
            }
         }
      }
      return builder.toString();  
   }

   private String searchDescription(DeltaRecord record) {
      StringBuilder builder = new StringBuilder();
      
      if(record != null) {
         Session session = record.getSession();
         String type = record.getType();
         
         builder.append("<h4>");
         builder.append(type);
         builder.append(" (");
         builder.append(session);
         builder.append(")");
         builder.append("</h4>");
      }
      return builder.toString();      
   }
   
   private String searchResult(DeltaRecord record) {
      TableDrawer table = new TableDrawer("attribute", "current", "previous", "format", "version");
      SortedSet<String> columns = new TreeSet<String>();

      if(record != null) {
         DeltaMerge merge = record.getMerge();
         Row currentRow = merge.getCurrent();
         Row previousRow = merge.getPrevious();
         Schema schema = merge.getSchema();
         int count = schema.getCount();
         
         for(int i = 0; i < count; i++) {
            Column column = schema.getColumn(i);
            String name = column.getName();
            
            columns.add(name);
         }
         for(String column : columns) {
            TableRowDrawer row = table.newRow();  
            Cell currentCell = currentRow.getCell(column);
            Cell previousCell = null;

            if(previousRow != null) {
               previousCell = previousRow.getCell(column);
            } 
            row.setBold("attribute", column);
            
            if(currentCell != null) {
               Object value = currentCell.getValue();
               Version version = currentCell.getVersion();
               
               row.setNormal("current", value);
               
               if(previousCell != null) {
                  Object previousValue = previousCell.getValue();
                  
                  if(previousValue != value) {
                     row.setColor("#00ff00");
                  }
                  row.setNormal("previous", previousValue);                     
               }  
               if(value != null) {
                  Class type = value.getClass(); 
                  String format = type.getName();
                  
                  row.setNormal("format", format);
               }                         
               row.setNormal("version", version);
            }
         }  
      }
      return table.drawTable();
   }   

   public void update(Session session, DeltaIndex index) { 
      indexes.cache(session, index);      
   }   
}
