package com.authrus.tuple.grid;

/**
 * A schema represents the structure of a grid which is composed of named
 * columns. Each {@link Column} within the grid contain an index which is 
 * used to identify what values are in a row.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.tuple.grid.ColumnBuilder
 */
public interface Schema {
   int getCount();
   Column getColumn(int index);
   Column getColumn(String name);
}
