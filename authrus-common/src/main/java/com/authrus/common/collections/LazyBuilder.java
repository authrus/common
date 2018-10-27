package com.authrus.common.collections;

public interface LazyBuilder<K, V> {
   V create(K key);
}
