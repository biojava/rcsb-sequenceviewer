package org.rcsb.sequence.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueInfo;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.SegmentedSequence;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.model.SequenceCollection;
import org.rcsb.sequence.model.SequenceSegment;


public class SegmentedSequenceImpl extends AbstractSequence implements SegmentedSequence, Serializable {
   
   private static final long serialVersionUID = 1L;
   
   public static final int DEFAULT_FRAGMENT_LENGTH = 60;
   
   protected final List<SequenceSegment> sequenceSegments = new ArrayList<SequenceSegment>();
   
   protected int fragmentLength;
   protected final Sequence  backingSequence;
   protected final ResidueNumberScheme rns;
   
   boolean isDestroyed = false;
   
   SegmentedSequenceImpl(Sequence sequence, ResidueNumberScheme residueNumberScheme, int fragmentLength)
   {
      super(sequence.getSequenceString());
      System.out.println("Segmenting " + sequence.getChainId() + " into frags of len " + fragmentLength);
      if(sequence == null || residueNumberScheme == null)
      {
         throw new NullPointerException();
      }
      this.fragmentLength = fragmentLength;
      this.rns = residueNumberScheme;
      this.backingSequence = sequence;
      
      reInitFragmentLists();
      System.out.println("done segmenting " + sequence.getChainId() +" " +  fragmentLength);
   }
   
   public void destroy(){
      super.destroy();
      
      // avoid circular method calls from sub-sequences...
      
      if ( isDestroyed)
         return;
      
      isDestroyed = true;
      destroySequenceSegments();
      backingSequence.destroy();
      
    
      
   }
   
   private void destroySequenceSegments(){
      for (SequenceSegment s : sequenceSegments){
         s.destroy();
      }
      sequenceSegments.clear();
   }
   
   public String  toString(){
      StringBuffer buf = new StringBuffer("SegmentedSequenceImpl: ");
      buf.append(fragmentLength);
      buf.append(" ");
      buf.append(rns);
      buf.append(" ");
      buf.append(backingSequence);
      buf.append(" ");
      buf.append(getSequenceString());
      return buf.toString();
     
   }

   public String getChainId() {
      return backingSequence.getChainId();
   }

   public int getSegmentLength() {
      return fragmentLength;
   }

   public void setSegmentLength(int fragmentLength) {
      if(this.fragmentLength != fragmentLength)
      {
         this.fragmentLength = fragmentLength;
         reInitFragmentLists();
      }
   }

   public List<SequenceSegment> getSequenceSegments() {
      return sequenceSegments;
   }
   
   // Regenerate fragment lists based on fragmentLength
   protected void reInitFragmentLists() {
      ensureResiduesInstantiated();
      if(backingSequence == null || rns == null)
      {
         throw new NullPointerException();
      }
      
      destroySequenceSegments();
      
      final int sequenceLength = backingSequence.getSequenceLength(rns);
      assert sequenceLength > 0 : "Chain " + backingSequence + " has no residues!!";
      final int numFragments   = ((sequenceLength - 1) / fragmentLength) + 1;
      
      ResidueId start = backingSequence.getFirstResidue(rns), end = start;
      int counter = 1;
      Iterator<ResidueId> residueIdIt = backingSequence.getResidueIds(rns).iterator();
      while(residueIdIt.hasNext())
      {
         end = residueIdIt.next();
         if(counter % fragmentLength == 0)
         {
            createFragment(start,end,fragmentLength, numFragments);
            if(!end.getNext().isEndOfChainMarker()) // i don't like doing this
            {
               start = end.getNext(); // prepare for next fragment
            }
            else
            {
               assert !residueIdIt.hasNext() : "We should have reached the end of a chain";
            }
         }
         counter++;
      }
      if(sequenceLength % fragmentLength != 0)
      {
         createFragment(start, end, fragmentLength, numFragments);
      }
      
      assert sequenceSegments.size() == numFragments;
      
   }
   
   private void createFragment(ResidueId start, ResidueId end, int maxLength, int numFragments)
   {
      int fragmentIdx = sequenceSegments.size() + 1;
      SequenceSegment aFragment = new SequenceSegmentImpl(this, start, end, fragmentIdx, maxLength, numFragments);
      sequenceSegments.add(aFragment);
   }
   
   public ResidueNumberScheme getResidueNumberScheme() 
   {
      return rns;
   }

   @Override
   public <T extends AnnotationGroup<?>> T getAnnotationGroup(Class<T> object) {
      return backingSequence.getAnnotationGroup(object);
   }

   public Collection<ResidueNumberScheme> getAvailableResidueNumberSchemes() {
      return backingSequence.getAvailableResidueNumberSchemes();
   }

   public ResidueId getFirstResidue(ResidueNumberScheme aRns) {
      return backingSequence.getFirstResidue(aRns);
   }

   @Override
   public Collection<ResidueId> getIdsForResidue(ResidueInfo r, ResidueNumberScheme aRns) {
      return backingSequence.getIdsForResidue(r, aRns);
   }

   @Override
   public ResidueId getLastResidue(ResidueNumberScheme aRns) {
      return backingSequence.getLastResidue(aRns);
   }

   public PolymerType getPolymerType() {
      return backingSequence.getPolymerType();
   }

   @Override
   public ResidueId getResidueId(ResidueNumberScheme aRns, String idAsString) {
      return backingSequence.getResidueId(aRns, idAsString);
   }

   @Override
   public ResidueId getResidueId(ResidueNumberScheme aRns, Integer id) {
      return backingSequence.getResidueId(aRns, id);
   }

   @Override
   public ResidueId getResidueId(ResidueNumberScheme aRns, Integer id, Character insertionCode) {
      return backingSequence.getResidueId(aRns, id, insertionCode);
   }

   @Override
   public Collection<ResidueId> getResidueIds(ResidueNumberScheme aRns) {
      return backingSequence.getResidueIds(aRns);
   }

   @Override
   public Collection<ResidueId> getResidueIdsBetween(ResidueId start, ResidueId end) {
      return backingSequence.getResidueIdsBetween(start, end);
   }

   public int getSequenceLength() {
      return backingSequence.getSequenceLength();
   }

   public int getSequenceLength(ResidueNumberScheme aRns) {
      return backingSequence.getSequenceLength(aRns);
   }

   public String getSequenceString() {
      return backingSequence.getSequenceString();
   }

   public String getSequenceString(ResidueNumberScheme aRns) {
      return backingSequence.getSequenceString(aRns);
   }

   public String getStructureId() {
      return backingSequence.getStructureId();
   }

//   @Override
//   public boolean hasAnnotationGroup(AnnotationName object) {
//      return chain.hasAnnotationGroup(object);
//   }

   @Override
   public boolean hasResiduesIndexedBy(ResidueNumberScheme residueNumberScheme) {
      return backingSequence.hasResiduesIndexedBy(residueNumberScheme);
   }
//
//   public boolean isAnnotated() {
//      return chain.getStatus() == ChainStatus.done;
//   }

   @Override
   public Collection<AnnotationGroup<?>> getAnnotationGroupsWithData() {
      return backingSequence.getAnnotationGroupsWithData();
   }

   @Override
   public Collection<AnnotationGroup<?>> getAvailableAnnotationGroups() {
      return backingSequence.getAvailableAnnotationGroups();
   }

   public void ensureAnnotated() {
      backingSequence.ensureAnnotated();
   }
   
   @Override
   protected void ensureResiduesInstantiated()
   {
      // do nothing
   }

   public ResidueNumberScheme getDefaultResidueNumberScheme() {
      return backingSequence.getDefaultResidueNumberScheme();
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

   public SequenceCollection getSequenceCollection() {
      return backingSequence.getSequenceCollection();
   }

   public Sequence getOriginalSequence() {
      return backingSequence;
   }

   public int getSegmentCount() {
      return sequenceSegments.size();
   }

   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = super.hashCode();
      result = PRIME * result + fragmentLength;
      result = PRIME * result + ((rns == null) ? 0 : rns.hashCode());
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
      final SegmentedSequenceImpl other = (SegmentedSequenceImpl) obj;
      if (fragmentLength != other.fragmentLength)
         return false;
      if (rns == null) {
         if (other.rns != null)
            return false;
      } else if (!rns.equals(other.rns))
         return false;
      return true;
   }

   public Chain getChain() {
      return backingSequence.getChain();
   }
   
   
}
