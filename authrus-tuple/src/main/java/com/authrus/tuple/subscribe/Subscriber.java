package com.authrus.tuple.subscribe;

import com.authrus.tuple.TupleListener;
import com.authrus.tuple.query.Query;

public interface Subscriber {
   Subscription subscribe(TupleListener listener, Query query);
}
