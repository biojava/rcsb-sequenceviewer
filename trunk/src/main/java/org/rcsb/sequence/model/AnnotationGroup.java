package org.rcsb.sequence.model;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.collections.Bag;
import org.rcsb.sequence.conf.AnnotationName;

/**
 * <p>An <tt>AnnotationGroup</tt> encapsulates all {@link Annotation}s of the same {@link AnnotationName} (for example, 'SCOP')
 * present on a particular {@link Sequence}.  </p>
 * 
 * <p><tt>AnnotationGroup</tt> implementations are responsible for querying the database for the 
 * required information to instantiate its <tt>Annotation</tt>s.  Because each annotation type requires different primary data,
 * the <tt>AnnotationGroup</tt>s for each <tt>AnnotationName</tt>
 * are implemented by different concrete implementations.  For example, SCOP annotations are implemented by {@link ScopAnnotationGroup}</p>
 * @author mulvaney
 *
 * @param <T> The ultimate value type to be returned by individual {@link Annotation}s.
 */
public interface AnnotationGroup<T> extends AnnotationInformation {

   /**
    * Get all {@link Annotation}s for this <tt>AnnotationGroup</tt>
    * @return a <tt>SortedSet</tt> of all annotations
    */
   public abstract SortedSet<Annotation<T>> getAnnotations();
   
   /**
    * Get the {@link Sequence} onto with all {@link Annotation}s are assigned
    * @return
    */
   public abstract Sequence getSequence();
   
   /** Since there is a circular reference between sequence and annotationgroup, we need to provide a destroy method so this object can get cleaned up properly...
    * 
    */
   //PDBWW-1753
   public abstract void destroy();
   
   /**
    * <p>Get the "value string" of this <tt>AnnotationGroup</tt>. For each {@link ResidueId} in 
    * the <tt>AnnotationGroup</tt>'s {@link Sequence}, a single character is emitted. This character
    * represents the annotation state of that <tt>ResidueId</tt> and is obtained by a call to {@link AnnotationValue#toCharacter()}.  Thus, the complete "value string"
    * can be trivially aligned with the sequence string in a text-mode display.</p>
    * <p>For an example of this in use, look at the source code for {@link TestSecondaryStructureAnnotationWith1ATP#testDsspAgainstVSequenceString()}</p>
    * @param rns
    * @return
    * @see Sequence#getSequenceString()
    * @see AnnotationGroup#getSequence()
    * @see AnnotationValue#toCharacter()
    */
   public abstract String getValueString(ResidueNumberScheme rns);
   
   /**
    * <p>Get the "value string" of this <tt>AnnotationGroup</tt>. For each {@link ResidueId} in 
    * the <tt>AnnotationGroup</tt>'s {@link Sequence}, a single character is emitted. This character
    * represents the annotation state of that <tt>ResidueId</tt> and is obtained by a call to {@link AnnotationValue#toCharacter()}.  Thus, the complete "value string"
    * can be trivially aligned with the sequence string in a text-mode display.</p>
    * <p>For an example of this in use, look at the source code for {@link TestSecondaryStructureAnnotationWith1ATP#testDsspAgainstVSequenceString()}</p>
    * @return
    * @see Sequence#getSequenceString()
    * @see AnnotationGroup#getSequence()
    * @see AnnotationValue#toCharacter()
    */
   public abstract String getValueString();

   /**
    * Are annotations allowed to overlap in this <tt>AnnotationGroup</tt>?  That is, may one residue
    * have more than one <tt>Annotation</tt> assigned to it?
    * @return
    */
   public abstract boolean annotationsMayOverlap();
   
   /**
    * Returns true if
    * <ul>
    * <li>{@link #annotationsMayOverlap()} would return true; and
    * <li>at least one {@link ResidueId} has more than one {@link Annotation} assigned to it
    * @return
    */
   public abstract boolean annotationsDoOverlap();
   
   /**
    * @return the maximum number of overlapping {@link Annotation}s assigned to a single {@link ResidueId}
    */
   public abstract int getMaxAnnotationsPerResidue();
   
   /**
    * Does this <tt>AnnotationGroup</tt> contain any {@link Annotation}s?
    * @return <tt>true</tt> if and only if the <tt>AnnotationGroup</tt> contains at least one <tt>Annotation</tt>
    */
   public abstract boolean hasData();
   
   /**
    * Get the {@link AnnotationStatus} of this <tt>AnnotationGroup</tt>.  It shouldn't be necessary to worry about this.
    * @return
    */
   public abstract AnnotationStatus getStatus();
   
   /**
    * Gets the {@link Annotation} for a given residue. If {@link #annotationsMayOverlap()} returns <tt>true</tt>,
    * this method returns the first <tt>Annotation</tt> found.
    * @param residueId
    * @return the first {@link Annotation} found that is assigned to the specified {@link ResidueId}
    */
   public abstract Annotation<T> getAnnotation(ResidueId residueId);
   
   /**
    * Get all {@link Annotation}s for a given {@link ResidueId}.
    * @param residueId
    * @return
    */
   public abstract Collection<Annotation<T>> getAnnotations(ResidueId residueId);
   
   /**
    * Does this <tt>AnnotationGroup</tt> have an annotation that covers the specified {@link ResidueId}?
    * @param residueId
    * @return
    */
   public abstract boolean annotatesResidue(ResidueId residueId);
   
   /**
    * Get the {@link Bag} that has a count of {@link Annotation}s for each {@link ResidueId}
    * @return
    */
   public abstract Bag getAnnotationsPerResidueBag();
   
   /**
    * Get the number of distinct {@link Annotation}s that cover a particular {@link ResidueId}
    * @param residueId
    * @return
    */
   public abstract int getAnnotationCount(ResidueId residueId);
   
   /**
    * Get the number of {@link ResidueId}s for each distinct {@link AnnotationValue}
    * @return
    */
   public abstract Map<AnnotationValue<T>, Integer> getResiduesPerAnnotationValue();
   
   /**
    * Get the number of occurrences of each {@link AnnotationValue}
    * @return
    */
   public abstract Map<AnnotationValue<T>, Integer> getAnnotationValueCount();
   
   
   /**
    * Do not call this method
    * @throws Exception
    */
   public abstract void constructAnnotations() throws Exception;
}
