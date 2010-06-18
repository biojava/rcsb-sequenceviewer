package org.rcsb.sequence.core;

import java.io.Serializable;

import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.Sequence;


public class BoundedAnnotation<T> implements Annotation<T>, Serializable
{
   private static final long serialVersionUID = 1L;
   private final Annotation<T> originalAnnotation;
   private final boolean isTruncated, beginningTruncated, endTruncated;
   private final Sequence sequence;
   
   BoundedAnnotation(Annotation<T> an, ResidueId lowerBound, ResidueId upperBound, int maxLength)
   {
      lowerBound.ensureBeforeOrEqual(upperBound);
      
      ResidueId min, max, anStart, anEnd, seqStart, seqEnd;
      ResidueNumberScheme rns = an.getResidueNumberScheme();
      
      min = lowerBound.getNextEquivalentResidueId(rns);
      max = upperBound.getPreviousEquivalentResidueId(rns);
      
      min.ensureBeforeOrEqual(max);
      
      this.originalAnnotation = an;
      
      anStart = an.getSequence().getFirstResidue();
      anEnd   = an.getSequence().getLastResidue ();
      beginningTruncated = anStart.isBefore(min);
      endTruncated = anEnd.isAfter(max);
      
      seqStart = beginningTruncated ? min : anStart;
      seqEnd   = endTruncated       ? max : anEnd;

      this.sequence = new DerivedSequence(seqStart, seqEnd);
      
      if(this.sequence.getSequenceLength() > maxLength)
      {
         throw new RuntimeException("Sequence longer than maximum allowed!");
      }
      
      isTruncated = beginningTruncated || endTruncated;
   }

   public AnnotationClassification getClassification() {
      return originalAnnotation.getClassification();
   }

   public AnnotationName getName() {
      return originalAnnotation.getName();
   }

   public ResidueNumberScheme getResidueNumberScheme() {
      return originalAnnotation.getResidueNumberScheme();
   }

   public Sequence getSequence() {
      return sequence;
   }

   public AnnotationValue<T> getAnnotationValue() {
      return originalAnnotation.getAnnotationValue();
   }
   
   public Sequence getFullSequence()
   {
      return originalAnnotation.getSequence();
   }
   
   public boolean isBeginningTruncated() {
      return beginningTruncated;
   }

   public boolean isEndTruncated() {
      return endTruncated;
   }

   public boolean isTruncated() {
      return isTruncated;
   }

   public Annotation<T> getOriginalAnnotation() {
      return originalAnnotation;
   }

   public boolean annotatesResidue(ResidueId theResidueId) {
      return sequence.containsResidue(theResidueId);
   }
   
   
}
