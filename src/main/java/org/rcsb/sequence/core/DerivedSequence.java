package org.rcsb.sequence.core;


import java.io.Serializable;
import java.util.Collection;

import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.ResidueUtils;
import org.rcsb.sequence.model.SequenceCollection;

public class DerivedSequence extends AbstractSequence implements Serializable {

	private static final long serialVersionUID = 1L;

   public DerivedSequence(ResidueId start, ResidueId end) {
	   super(getSequenceString(ResidueUtils.getResidueIdsBetween(start, end)));
      
      ResidueUtils.ensureResiduesComparable(start, end);
      if(start.isAfter(end))
      {
         throw new RuntimeException("Trying to add an annotation backwards is not allowed :" + start + " > " + end);
      }
      
      this.backingChain = start.getChain();
      this.residueNumberScheme = start.getResidueNumberScheme();
      
      ResidueUtils.initDerivedResidueIdMap(getResidueIdMaps(), start, end);
   }
   
   public DerivedSequence(ResidueId theResidueId) {
      this(theResidueId, theResidueId);
   }
   
	private final Chain backingChain;
   private final ResidueNumberScheme residueNumberScheme;

   public String getChainId() {
      return backingChain.getChainId();
   }

   public ResidueNumberScheme getDefaultResidueNumberScheme() {
      return residueNumberScheme;
   }

   public SequenceCollection getSequenceCollection() {
      return backingChain.getSequenceCollection();
   }

   public void ensureAnnotated() {
      // do nothing
   }

   @Override
   protected void ensureResiduesInstantiated() {
      // do nothing
   }

   public Chain getChain() {
      return backingChain;
   }

   public Collection<ResidueNumberScheme> getAvailableResidueNumberSchemes() {
      return backingChain.getAvailableResidueNumberSchemes();
   }

   public String getExternalDbCode() {
      return backingChain.getExternalDbCode();
   }

   public String getExternalDbName() {
      return backingChain.getExternalDbName();
   }

   public Character getPdbChainId() {
      return backingChain.getPdbChainId();
   }

   public PolymerType getPolymerType() {
      return backingChain.getPolymerType();
   }

   public String getStructureId() {
      return backingChain.getStructureId();
   }
}