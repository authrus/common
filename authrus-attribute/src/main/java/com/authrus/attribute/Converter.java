package com.authrus.attribute;

/**
 * The convert is used to read and write attributes to and from an object
 * instance. This allows an object graph to be recursively traversed and
 * converted in to key value pairs.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.attribute.CompositeConverter
 */
public interface Converter {
   void readAttributes(AttributeReader reader, Object object, String name, Class[] dependents) throws Exception;
   void writeAttributes(AttributeWriter writer, Object object, String name, Class[] dependents) throws Exception;
}
