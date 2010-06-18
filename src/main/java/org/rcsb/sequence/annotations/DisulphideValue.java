package org.rcsb.sequence.annotations;

import java.io.Serializable;

import org.rcsb.sequence.core.AbstractAnnotationValue;
import org.rcsb.sequence.model.ResidueId;


public class DisulphideValue extends AbstractAnnotationValue<ResidueId> implements Serializable {
   
   /**
    * 
    */
   private static final long serialVersionUID = 4075350521307278791L;
   public final ResidueId annotatedRes, connectedRes;
   public final Float distance;
   public final Boolean betweenChains;
   
   public DisulphideValue(ResidueId annotated, ResidueId connected, Float distance)
   {
      this.annotatedRes  = annotated;
      this.connectedRes  = connected;
      this.distance      = distance;
      this.betweenChains = annotated.getChain().getChainId().equals(connected.getChain().getChainId());
   }
   
   public String getDescription() {
      return "Disulphide bond between " + annotatedRes + " and " + connectedRes;
   }

   public Character toCharacter() {
      return 'C';
   }
   
   @Override
   public String toString()
   {
      return getDescription();
   }

   public ResidueId value() {
      return connectedRes;
   }
   
}
