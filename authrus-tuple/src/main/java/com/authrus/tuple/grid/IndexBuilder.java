package com.authrus.tuple.grid;

interface IndexBuilder<T extends Index> {
   T createIndex(String name, Version version, int index);
}
