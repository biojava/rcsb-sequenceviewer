package org.rcsb.sequence.core;

import java.io.Serializable;

import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.AnnotationInformation;
import org.rcsb.sequence.model.ResidueNumberScheme;



public abstract class AnnotationInformationImpl implements AnnotationInformation, Serializable {

   private static final long serialVersionUID = 1L;
   protected final AnnotationClassification classification;
   protected final AnnotationName name;
   protected final ResidueNumberScheme residueNumberScheme;
   
   AnnotationInformationImpl(AnnotationClassification classification, AnnotationName name, ResidueNumberScheme rns)
   {
      if(name.getClassification() != classification)
      {
         throw new RuntimeException("Annotation " + name + " is not compatible with classification " + classification + ". It should be classification " + name.getClassification());
      }
      
      this.classification      = classification;
      this.name                = name;
      this.residueNumberScheme = rns;
   }
   
   public AnnotationClassification getClassification() {
      return classification;
   }
   public AnnotationName getName() {
      return name;
   }
   public ResidueNumberScheme getResidueNumberScheme() {
      return residueNumberScheme;
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
        
        retValue.append("AbstractAnnotationImpl ( ")
            .append(super.toString()).append(TAB)
            .append("classification = ").append(this.classification).append(TAB)
            .append("name = ").append(this.name).append(TAB)
            .append("residueNumberScheme = ").append(this.residueNumberScheme).append(TAB)
            .append(" )");
        
        return retValue.toString();
    }
   
   
}
