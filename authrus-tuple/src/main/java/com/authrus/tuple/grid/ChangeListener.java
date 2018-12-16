package com.authrus.tuple.grid;

/**
 * A change listener receives updates from a {@link Grid} when there has been a
 * change. It can be used to capture the change from the grid or perform some
 * other operation that is dependent on the state of the grid. Typically deltas
 * are generated on a grid update.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.tuple.grid.DeltaProducer
 */
public interface ChangeListener {
   void onChange(Grid grid, Schema schema, String type);
}
