package com.authrus.tuple.grid;

class RotateIndex<T extends Index> {

   private final T allocate;
   private final T delete; 
   
   public RotateIndex(T allocate, T delete) {
      this.allocate = allocate;
      this.delete = delete;
   }
   
   public T getAllocate() {
      return allocate;
   }
   
   public T getDelete() {
      return delete;
   }
}
