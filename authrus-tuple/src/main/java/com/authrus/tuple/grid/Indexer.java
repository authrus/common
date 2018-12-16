package com.authrus.tuple.grid;

interface Indexer<T extends Index> extends Iterable<T> {
   boolean isEmpty();
   boolean contains(String name);
   T index(String name);
   int size();
}
