package org.rcsb.sequence.core;

import java.io.Serializable;

import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.Sequence;

public class AnnotationImpl<T> extends AnnotationInformationImpl
                            implements Annotation<T>, Serializable
{
   private static final long serialVersionUID = 1L;
   private final Sequence annotatedSequence;
   private final AnnotationValue<T> theValue;
   
   public AnnotationImpl(AnnotationClassification classification, AnnotationName name,
         ResidueNumberScheme rns, AnnotationValue<T> value, ResidueId start, ResidueId end)
   {
      super(classification, name, rns);
      this.theValue = value;
      this.annotatedSequence = new DerivedSequence(start, end);
   }
   
   public Sequence getSequence() {
      return annotatedSequence;
   }
   
   public AnnotationValue<T> getAnnotationValue()
   {
      return theValue;
   }

   /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation 
     * of this object.
     */
    @Override
   public String toString()
    {
        final String TAB = "    ";
    
        StringBuilder retValue = new StringBuilder();
        
        retValue.append("Annotation ( ")
            .append(super.toString()).append(TAB)
            .append("theValue = ").append(this.theValue).append(TAB)
            .append(" )");
        
        return retValue.toString();
    }

   public boolean isBeginningTruncated() {
      return false;
   }

   public boolean isEndTruncated() {
      return false;
   }

   public boolean isTruncated() {
      return false;
   }

   public boolean annotatesResidue(ResidueId theResidueId) {
      if(theResidueId.getResidueNumberScheme() != getResidueNumberScheme())
      {
         theResidueId = theResidueId.getEquivalentResidueId(getResidueNumberScheme());
      }
      return annotatedSequence.containsResidue(theResidueId);
   }

}
