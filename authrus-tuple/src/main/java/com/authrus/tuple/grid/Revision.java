package com.authrus.tuple.grid;

/**
 * This provides a read only copy of a version. Typically is is used
 * to provide a handle on the current table version without the
 * ability to modify it. Uses include the {@link Key} indexes which
 * used it to ensure rapidly recycled indexes to not pick up old rows.
 * 
 * @author Niall Gallagher
 */
public class Revision {

   private final Version version;
   
   public Revision(Version version) {
      this.version = version;    
   }
   
   public Version getCurrent() {
      return version.copy();
   }
}
