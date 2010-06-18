package org.rcsb.sequence.model;

import java.util.List;


/**
 * Extension to the {@link Sequence} interface representing a <tt>Sequence</tt> that is
 * divided into {@link SequenceSegment}s of fixed length
 * @author mulvaney
 *
 */
public interface SegmentedSequence extends Sequence {

   /**
    * Gets the length of {@link SequenceSegment}s
    * @return
    */
   public abstract int getSegmentLength();
   
   /**
    * Resets the length of {@link SequenceSegment}s
    * @param fragmentLength
    */
   public abstract void setSegmentLength(int fragmentLength);
   
   /**
    * Gets the {@link SequenceSegment}s that comprise this <tt>SegmentedSequence</tt>
    * @return
    */
   public abstract List<SequenceSegment> getSequenceSegments();
   
   /**
    * Gets a count of the number of segments in this <tt>SegmentedSequence</tt>
    * @return
    */
   public abstract int getSegmentCount();
   
   /**
    * Get the original, unsegmented {@link Sequence} from which this <tt>SegmentedSequence</tt> was derived
    * @return
    */
   public abstract Sequence getOriginalSequence();
   
}
