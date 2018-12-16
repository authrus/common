package com.authrus.tuple.subscribe;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.query.Query;

/**
 * A subscription represents a connection to a subscription server that can be
 * updated to filter data. At any time the subscription can be cancelled which
 * will typically close the connection.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.tuple.subscribe.Subscriber
 */
public interface Subscription { 
   void publish(Tuple tuple);      
   void update(Query query); 
   void cancel();
}
