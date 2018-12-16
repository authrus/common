package com.authrus.attribute.string;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.authrus.attribute.AttributePersister;
import com.authrus.attribute.AttributeSerializer;

public class StringPersister implements AttributePersister<String> {

   private final StringMarshaller marshaller;

   public StringPersister(AttributeSerializer serializer) {
      this.marshaller = new StringMarshaller(serializer);
   }

   @Override
   public String toState(Object object) throws IOException {
      try {
         Map<String, String> message = marshaller.toMessage(object);
         Set<String> keys = message.keySet();
         
         if(!keys.isEmpty()) {
            StringElementConverter converter = new StringElementConverter(message);
            StringWriter writer = new StringWriter();
   
            converter.write(writer);
            
            return writer.toString();
         }
      } catch(Exception e) {
         throw new IllegalArgumentException("Could not save '" + object + "'", e);
      }  
      return null;
   }

   @Override
   public Object fromState(String state) throws IOException {      
      try {
         if(state != null) {              
            Map<String, String> attributes = new HashMap<String, String>();
            StringElementConverter converter = new StringElementConverter(attributes);
            StringReader reader = new StringReader(state);
   
            converter.read(reader);
            
            return marshaller.fromMessage(attributes);
         }
      } catch(Exception e) {
         throw new IllegalArgumentException("Could not parse '" + state + "'", e);
      }      
      return null;
   }
}
