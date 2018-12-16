package com.authrus.message.bind;

import java.util.Map;

public interface ObjectMarshaller<T> {
   Map<String, Object> fromObject(T object);
   T toObject(Map<String, Object> message);
}
