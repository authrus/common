package com.authrus.tuple.queue;

/**
 * This listener is called when an object is added to a queue. If the listener
 * is interested in elements of the specified type then it will typically send
 * the element over a connection.
 * 
 * @author Niall Gallagher
 */
public interface InsertListener {
   void onInsert(ElementBatch batch, String type);
}
