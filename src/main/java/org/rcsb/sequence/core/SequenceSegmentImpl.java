package org.rcsb.sequence.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueInfo;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.ResidueUtils;
import org.rcsb.sequence.model.SegmentedSequence;
import org.rcsb.sequence.model.SequenceCollection;
import org.rcsb.sequence.model.SequenceSegment;

public class SequenceSegmentImpl extends AbstractSequence implements SequenceSegment, Serializable
{
   private static final long serialVersionUID = 1L;
   
   protected final int fragmentNumber;
   protected SegmentedSequence backingSequence;
   protected final ResidueId lowerBound;
   protected final ResidueId upperBound;
   protected final ResidueNumberScheme rns;
   protected final int maxLength;
   protected final int numFragments;
   private final Collection<ResidueId> residueIds;
   //protected SequenceSegmentImpl next, previous;
   
   protected SequenceSegmentImpl(SegmentedSequence sequence, ResidueId lowerBound, ResidueId upperBound, int fragmentIdx, int maxLength, int numFragments) 
   {
      super(getSequenceString(ResidueUtils.getResidueIdsBetween(lowerBound, upperBound)));
      //System.out.println("creating sequenceSegmentImpl " + lowerBound + " " + upperBound);
      ResidueUtils.ensureResiduesComparable(lowerBound, upperBound);
      ResidueUtils.initDerivedResidueIdMap(getResidueIdMaps(), lowerBound, upperBound);
      this.backingSequence = sequence;
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
      
      this.rns = lowerBound.getResidueNumberScheme();
      
      this.fragmentNumber = fragmentIdx;
      
      this.residueIds = ResidueUtils.getResidueIdsBetween(lowerBound, upperBound);
      
      this.maxLength = maxLength;
      this.numFragments = numFragments;
      
      assert this.residueIds.size() == getSequenceString().length();
      
      initAnnotationGroupFragments();
   }
   
   public void destroy(){
      
      backingSequence.destroy();
      residueIds.clear();      
      backingSequence = null;
      
   }
   
   @SuppressWarnings("unchecked")
   private void initAnnotationGroupFragments()
   {
      for(AnnotationGroup<?> ag : backingSequence.getAvailableAnnotationGroups())
      {
         getAnnotationGroupMap().put((Class<AnnotationGroup<?>>) ag.getClass(), ag.getName().createAnnotationGroupInstance(this));
      }
   }
   
   @Override
   @SuppressWarnings("unchecked")
   public Collection<ResidueId> getResidueIds()
   {
      return CollectionUtils.select(backingSequence.getResidueIds(rns), new BoundedResiduePredicate());
   }
   
   private class BoundedResiduePredicate implements Predicate, Serializable
   {
      private static final long serialVersionUID = 1L;

      public boolean evaluate(Object arg0) {
         boolean result = false;
         if(arg0 instanceof ResidueId)
         {
            ResidueId aRes = (ResidueId)arg0;
            if(!ResidueUtils.areResiduesComparable(aRes, lowerBound))
            {
               throw new RuntimeException("Can't compare " + aRes + " to " + lowerBound);
            }
            else if(lowerBound == aRes || upperBound == aRes)
            {
               result = true;
            }
            else if(lowerBound.compareTo(aRes) < 0 && upperBound.compareTo(aRes) > 0)
            {
               result = true;
            }
         }
         return result;
      }
      
      protected BoundedResiduePredicate()
      {
         super();
      }
   }

   public SegmentedSequence getFullSequence() {
      return backingSequence;
   }

   public ResidueId getLowerBound() {
      return lowerBound;
   }
   
   public ResidueNumberScheme getDefaultResidueNumberScheme()
   {
      return rns;
   }

   public ResidueId getUpperBound() {
      return upperBound;
   }

   public Collection<ResidueNumberScheme> getAvailableResidueNumberSchemes() {
      return backingSequence.getAvailableResidueNumberSchemes();
   }

   public String getChainId() {
      return backingSequence.getChainId();
   }

   @Override
   public Collection<ResidueId> getIdsForResidue(ResidueInfo r, ResidueNumberScheme aRns) {
      throw new UnsupportedOperationException();
   }

   public PolymerType getPolymerType() {
      return backingSequence.getPolymerType();
   }

   @Override
    public ResidueId getResidueId(ResidueNumberScheme aRns, Integer id, Character insertionCode) {
      ResidueId result = backingSequence.getResidueId(aRns, id, insertionCode);
      result.ensureBetween(lowerBound, upperBound);
      return result;
   }

   @Override
   public ResidueId getResidueId(ResidueNumberScheme aRns, Integer id) {
      ResidueId result = backingSequence.getResidueId(aRns, id);
      result.ensureBetween(lowerBound, upperBound);
      return result;
   }

   @Override
   public ResidueId getResidueId(ResidueNumberScheme aRns, String idAsString) {
      ResidueId result = backingSequence.getResidueId(aRns, idAsString);
      result.ensureBetween(lowerBound, upperBound);
      return result;
   }

   @Override
   public Collection<ResidueId> getResidueIds(ResidueNumberScheme aRns) {
      ResidueId start = lowerBound.getNextEquivalentResidueId(aRns);
      ResidueId end   = upperBound.getPreviousEquivalentResidueId(aRns);
      
      // check for nulls or evidence there are no equivalent residues on the fragment and return an empty set if found
      if(start == null || end == null 
            || end.isBefore(start)
            || lowerBound.isAfter(start.getEquivalentResidueId(this.rns)) 
            || upperBound.isBefore(end.getEquivalentResidueId(this.rns)))
      {
         return Collections.emptySet();
      }
      
      // check for sillyness that suggests a bug in the code and throw an exception
      start.ensureBeforeOrEqual(end);
      
      return backingSequence.getResidueIdsBetween(start, end);
   }

   @Override
   public Collection<ResidueId> getResidueIdsBetween(ResidueId start, ResidueId end) {
      start.ensureBetween(lowerBound, upperBound);
      end.ensureBetween(lowerBound, upperBound);
      return backingSequence.getResidueIdsBetween(start, end);
   }

   public int getSequenceLength(ResidueNumberScheme aRns) {
      throw new UnsupportedOperationException();
   }

   public String getSequenceString(ResidueNumberScheme aRns) {
      throw new UnsupportedOperationException();
   }

   public String getStructureId() {
      return backingSequence.getStructureId();
   }

   @Override
   public boolean hasResiduesIndexedBy(ResidueNumberScheme residueNumberScheme) {
      return backingSequence.hasResiduesIndexedBy(residueNumberScheme);
   }

   @SuppressWarnings("unchecked")
   @Override
   public Collection<AnnotationGroup<?>> getAnnotationGroupsWithData() {
      return CollectionUtils.select(getAvailableAnnotationGroups(), new Predicate() {
         public boolean evaluate(Object arg0) {
            return arg0 instanceof AnnotationGroup && ((AnnotationGroup)arg0).hasData();
         }
      });
   }

   public void ensureAnnotated() {
      backingSequence.ensureAnnotated();
   }

   public SequenceCollection getSequenceCollection() {
      return backingSequence.getSequenceCollection();
   }

   public Chain getChain() {
      return backingSequence.getChain();
   }
   
   @Override
   protected void ensureResiduesInstantiated()
   {
      // do nothing
   }

   public int getFragmentIdx() {
      return fragmentNumber;
   }

   public int getMaxLength() {
      return maxLength;
   }

   public int getNumFragments() {
      return numFragments;
   }

   public Character getPdbChainId() {
      return backingSequence.getPdbChainId();
   }

   public String getExternalDbCode() {
      return backingSequence.getExternalDbCode();
   }

   public String getExternalDbName() {
      return backingSequence.getExternalDbName();
   }

   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = super.hashCode();
      result = PRIME * result + fragmentNumber;
      result = PRIME * result + maxLength;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final SequenceSegmentImpl other = (SequenceSegmentImpl) obj;
      if (fragmentNumber != other.fragmentNumber)
         return false;
      if (maxLength != other.maxLength)
         return false;
      return true;
   }
}