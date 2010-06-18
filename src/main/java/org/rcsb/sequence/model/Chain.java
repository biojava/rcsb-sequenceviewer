package org.rcsb.sequence.model;


/**
 * A <tt>Chain</tt> is a {@link Sequence} that represents a whole chain of a structure in the PDB.
 * @author mulvaney
 *
 */
public interface Chain extends Sequence, Comparable<Chain> 
{
   /**
    * Get the <tt>Chain</tt>'s entityId.
    * @return
    */
   public abstract Integer getEntityId();
}
