package com.authrus.common.lease;

public interface Cleaner<T> {
   void clean(T key) throws Exception;
}
