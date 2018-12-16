package com.authrus.tuple;

/**
 * An update listener is used to receive updates from a grid. The
 * update issued contains a message that has been reconstituted from 
 * a {@link Row} within the grid. When a connection opens or closes 
 * a reset notification is issued.
 * 
 * @author Niall Gallagher
 */
public interface TupleListener {
   void onUpdate(Tuple tuple);
   void onException(Exception cause);
   void onHeartbeat();
   void onReset();
}