package com.authrus.attribute;

import java.util.List;

/**
 * An object node holds static information on an object and its fields. This
 * allows an object graph to be traversed and a manner that allows it to be
 * marshalled in to key value pairs.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.attribute.ObjectScanner
 */
public interface ObjectNode {
   boolean isTransform();
   boolean isPrimitive();
   Object getInstance();
   Object getInstance(Class type);
   Converter getConverter(ObjectGraph graph);
   List<FieldAccessor> getFields();
   Class getType();
}
