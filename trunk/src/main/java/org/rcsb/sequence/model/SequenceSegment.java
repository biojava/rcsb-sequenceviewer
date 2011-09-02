package org.rcsb.sequence.model;



/**
 * A <tt>SequenceSegment</tt> is an individual <em>segment</em> of a {@link SegmentedSequence}. It behaves identiacally to a normal
 * {@link Sequence}, with additional methods providing information about how the sequence was segmented.
 * @author mulvaney
 * @see SegmentedSequence
 */
public interface SequenceSegment extends Sequence 
{
   /**
    * Get the full sequence, of which this is a segment
    * @return
    */
   public SegmentedSequence getFullSequence();
   
   /**
    * Get this sequence's 1-based fragment index.  
    * @return
    */
   public int getFragmentIdx();
   
   /**
    * Get the maximum allowed lengths of <tt>SequenceSegments</tt> in the {@link SegmentedSequence}
    * @return
    */
   public int getMaxLength();
   
   /**
    * Get the number of fragments in the {@link SegmentedSequence}
    * @return
    */
   public int getNumFragments();
}
