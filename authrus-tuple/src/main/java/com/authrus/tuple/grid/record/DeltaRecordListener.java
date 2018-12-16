package com.authrus.tuple.grid.record;

import com.authrus.tuple.frame.Session;

/**
 * Records delta merges before they are dispatched over a connection.
 * Any implementation can use this to determine what the receiver will
 * see and can be useful in tracing and debugging issues from the 
 * source service. All records contain the previous and current rows
 * for a particular object of a specific type.
 * 
 * @author Niall Gallagher
 */
public interface DeltaRecordListener {
   void onUpdate(Session session, DeltaRecord record);
   void onReset(Session session);
}
