package com.authrus.message.bind;

import java.util.Map;

import com.authrus.message.Message;

public class MessageBinder {

   private final ObjectBinder binder;
   private final String type;

   public MessageBinder(ObjectBinder binder, String type) {
      this.binder = binder;
      this.type = type;
   }

   public Message toMessage(Map<String, Object> attributes) {
      Object value = binder.toObject(attributes, type);

      if (!attributes.isEmpty()) {
         return new Message(value, attributes);
      }
      return new Message(value);
   }

   public Map<String, Object> fromMessage(Message message) {
      Object value = message.getValue();
      Map<String, Object> attributes = message.getAttributes();
      Map<String, Object> data = binder.fromObject(value, type);

      if (!attributes.isEmpty()) {
         data.putAll(attributes);
      }
      return data;
   }
}
