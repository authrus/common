package com.authrus.attribute;

import java.io.IOException;

public interface AttributePersister<T> {
   T toState(Object value) throws IOException;
   Object fromState(T state) throws IOException;
}
