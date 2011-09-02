package org.rcsb.sequence.model;


/**
 * An <tt>Annotation</tt> maps a single {@link AnnotationValue} onto a {@link Sequence}.   
 * 
 * @author mulvaney
 *
 * @param <T> The ultimate value type to be returned by each <tt>Annotation</tt>'s {@link AnnotationValue}s.
 */
public interface Annotation<T> extends AnnotationInformation {
   
   /**
    * Get the {@link AnnotationValue}.
    * @return
    */
   public abstract AnnotationValue<T> getAnnotationValue();
   
   /**
    * Get the {@Sequence} annotated.
    * @return
    */
   public abstract Sequence getSequence();
   
   /**
    * Does this <tt>Annotation</tt> annotate the specified {@link ResidueId}?
    * @param theResidueId
    * @return
    */
   public abstract boolean annotatesResidue(ResidueId theResidueId);

   /**
    * In some cases the {@link Sequence} returned by {@link #getSequence()} may not
    * span the entire <tt>Annotation</tt>. For example, if the sequence was <em>segmented</em>,
    * a single <tt>Annotation</tt> might span two or more {@link SequenceSegment}s.  
    * @return <tt>true</tt> if this <tt>Annotation</tt> instance is truncated for the reason mentioned above
    * @see Sequence#getSegmentedSequence(int, ResidueNumberScheme)
    */
   public abstract boolean isTruncated();
   
   /**
    * Is the beginning of this <tt>Annotation</tt> truncated?
    * @return
    * @see #isTruncated()
    */
   public abstract boolean isBeginningTruncated();
   
   /**
    * Is the end of this <tt>Annotation</tt> truncated?
    * @return
    * @see #isTruncated()
    */
   public abstract boolean isEndTruncated();
   
}
