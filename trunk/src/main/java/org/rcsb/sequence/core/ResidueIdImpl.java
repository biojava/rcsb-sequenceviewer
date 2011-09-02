package org.rcsb.sequence.core;

import static org.rcsb.sequence.model.ResidueNumberScheme.ATOM;
import static org.rcsb.sequence.model.ResidueNumberScheme.DBREF;
import static org.rcsb.sequence.model.ResidueNumberScheme.SEQRES;
import static org.rcsb.sequence.model.ResidueUtils.areResiduesComparable;
import static org.rcsb.sequence.model.ResidueUtils.ensureResiduesComparable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueInfo;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.ResidueType;

public final class ResidueIdImpl implements ResidueId, Serializable, Comparable<ResidueId> {
   
   private static final long serialVersionUID = 1L;
   
   public static final ResidueId BEGINNING_OF_CHAIN = new ResidueIdImpl();
   public static final ResidueId       END_OF_CHAIN = new ResidueIdImpl();
   
   private ResidueIdImpl()
   {
      this.residueNumberScheme = null;
      this.chain = null;
      this.residueInfo = null;
      this.seqId = null;
      this.monId = null;
      this.insertionCode = null;
      this.equivalentResidues = new EquivalentResidues(true);
   }
   
   public ResidueIdImpl(ResidueIdImpl residueToCopy, ResidueNumberScheme rns, Integer newSeqId)
   {
      this(residueToCopy, rns, null, newSeqId);
   }
   
   public ResidueIdImpl(ResidueIdImpl residueToCopy, ResidueNumberScheme rns, String newMonId, Integer newSeqId)
   {
      if(rns == residueToCopy.getResidueNumberScheme())
      {
         throw new RuntimeException("Residue " + residueToCopy + " is being duplicated");
      }
      
      // get this from constructor argument
      this.residueNumberScheme = rns;
      
      // decide where to get this
      this.residueInfo = newMonId == null ? residueToCopy.getResidueInfo() : ResidueProvider.getResidue(newMonId);
      this.seqId = newSeqId == null ? residueToCopy.getSeqId() : newSeqId;
      
      this.monId = residueInfo.getMonId();
      
      // copy everything else from specified residueId
      this.chain = residueToCopy.getChain();
      this.insertionCode = residueToCopy.getInsertionCode();
      
      residueToCopy.addEquivalentResidue(rns, this); // also populates equiv residues of this residueId instance
   }
   
   public ResidueIdImpl(ResidueNumberScheme rns, Chain c, Integer residueId, ResidueInfo residueInfo)
   {
      this(rns, c, residueId, residueInfo, null);
   }
   
   public ResidueIdImpl(ResidueNumberScheme rns, Chain c, Integer residueId, ResidueInfo residueInfo, ResidueIdImpl anEqResidueId) 
   {
      this(rns, c, residueId, null, residueInfo, anEqResidueId);
   }
   
   public ResidueIdImpl(ResidueNumberScheme rns, Chain c, Integer residueId, Character insertionCode, ResidueInfo residueInfo, 
         ResidueIdImpl anEquivalentResidue) 
   {
      this.residueNumberScheme = rns;
      this.chain = c;
      this.residueInfo = residueInfo;
      this.monId = residueInfo.getMonId();
      this.seqId = residueId;
      this.insertionCode = insertionCode;
      
      
      //System.out.println("new residueIdImpl: " + toString());
      // if we have a non-null insertion code, we should make sure that it's allowed (e.g. this shouldn't be mmcif)
      if(this.insertionCode != null && !residueNumberScheme.hasInsertionCodes())
      {
         throw new RuntimeException(residueNumberScheme + " residues should not have insertion codes");
      }
      
      if(anEquivalentResidue == null)
      {
         this.equivalentResidues = new EquivalentResidues();
      }
      else
      {
         this.equivalentResidues = anEquivalentResidue.equivalentResidues;
      }
      if(this.equivalentResidues != Collections.EMPTY_MAP)
      {
         this.equivalentResidues.put(rns, this);
      }
   }
   
	/**
	 * @uml.property  name="residueNumberScheme"
	 * @uml.associationEnd  readOnly="true" inverse="seqId:org.rcsb.sequence.ResidueNumberScheme"
	 * @uml.association  name="according to scheme"
	 */
   private final ResidueNumberScheme residueNumberScheme;
   private final Chain chain;
   private final String monId;
   private  ResidueInfo residueInfo;
   private final Integer seqId;
   private final Character insertionCode;
   
   private EquivalentResidues equivalentResidues;
   
   private ResidueId next     = END_OF_CHAIN;
   private ResidueId previous = BEGINNING_OF_CHAIN;
   
	/**
	 * Getter of the property <tt>residueNumberScheme</tt>
	 * @return  Returns the residueNumberScheme.
	 * @uml.property  name="residueNumberScheme"
	 */
	public ResidueNumberScheme getResidueNumberScheme() {
		return residueNumberScheme;
	}
   
	public Chain getChain() {
      return chain;
   }
   
   public Integer getSeqId() {
      return seqId;
   }
   
   private String seqIdWithInsertionCode = null;
   public String getSeqIdWithInsertionCode()
   {      
      if(seqIdWithInsertionCode == null)
      {
         seqIdWithInsertionCode = String.valueOf(seqId);
         if(hasInsertionCode()) seqIdWithInsertionCode += insertionCode;
      }
      
      return seqIdWithInsertionCode;
   }
   
   public boolean hasInsertionCode()
   {
      return insertionCode != null;
   }
   
   public Character getInsertionCode()
   {
      return insertionCode;
   }

   public PolymerType getPolymerType()
   {
      return chain.getPolymerType();
   }
   
   public ResidueType getResidueType()
   {
      return residueInfo.getType();
   }

   public ResidueInfo getResidueInfo() {
      if(residueInfo == null)
      {
         residueInfo = ResidueProvider.getResidue(monId);
      }
      return residueInfo;
   }
   
   public ResidueId getNext() {
      return next;
   }
   
   public boolean hasNext() {
      return next != null && next != END_OF_CHAIN;
   }

   public void setNext(ResidueId next) {
      if(this == BEGINNING_OF_CHAIN || this == END_OF_CHAIN) return;
      this.next = next;
   }

   public ResidueId getPrevious() {
      return previous;
   }
   
   public boolean hasPrevious() {
      return previous != null && previous != BEGINNING_OF_CHAIN;
   }

   public void setPrevious(ResidueId previous) {
	//   System.out.println("ResidueIdImpl: " + this + " setPrevious "  +previous);
      if(this == BEGINNING_OF_CHAIN || this == END_OF_CHAIN) return;
      this.previous = previous;
   }

   public ResidueId getEquivalentResidueId(ResidueNumberScheme rns)
   {
      return equivalentResidues.get(rns);
   }
   
   public void addEquivalentResidue(ResidueNumberScheme rns, ResidueIdImpl rid)
   {
      if(rns == null || rid == null) throw new NullPointerException();
      if(equivalentResidues == Collections.EMPTY_MAP)
      {
         //System.out.println("Replacing empty map with proper one (although it only contains itself right now) for equivalent residues of " + this);
         equivalentResidues = new EquivalentResidues();
         equivalentResidues.put(residueNumberScheme, this);
      }
      
     
     // System.out.println("adding equivalent " +rns + " residueNumberScheme " + rid + " " + equivalentResidues);
      
      
      if(!equivalentResidues.containsKey(rns)) 
      {
         equivalentResidues.put(rns, rid);
      }
      else
      {
         System.err.println("Not adding residue " + rid + " as equiv for " + rns + " because it has already been assigned for " + this);
      }
      
      if(rid.equivalentResidues != this.equivalentResidues)
      {
//         System.out.println("Overwriting the equivalentresiduemap of " + rid + " with that of " + this);
         rid.equivalentResidues = this.equivalentResidues;
      }
   }
   
   public Collection<ResidueId> getEquivalentResidueIds()
   {
      return Collections.unmodifiableCollection(equivalentResidues.values());
   }
   
//   Map<ResidueNumberScheme, ResidueId> getEquvalentResidueIdMap()
//   {
//      return equivalentResidues;
//   }
   
   void setEquivalentResidueIdMapFromEquivalentResidue(ResidueIdImpl equivRes)
   {
      this.equivalentResidues = equivRes.equivalentResidues;
   }
   
   /**
    * Traverse this residue id's sequence in the forward direction (i.e. towards the end) to find
    * the first residue with an equivalent residue id in the specified 
    * ResidueNumberScheme.
    * @param rns the found residue or ResidueId.END_OF_CHAIN if the end of the chain is found before an equivalent residue
    * @return
    */
   public ResidueId getNextEquivalentResidueId(ResidueNumberScheme rns)
   {
      ResidueId result, aResidue = this;
     // int count = 0;
      while((result = aResidue.getEquivalentResidueId(rns)) == null)
      {
         aResidue = aResidue.getNext();
         this.ensureBefore(aResidue);
         //System.out.println("getNextEquivalentResidueId call " + ++count);
      }
      //System.out.println("getNextEquivalentresidueId: " + toString() + " " + rns  + "  " + result + " " + result.getPrevious());
      return result;
   }

   
   /**
    * Traverse this residue id's sequence in the reverse direction (i.e. towards the start) to find
    * the first residue with an equivalent residue id in the specified 
    * ResidueNumberScheme.
    * @param rns the found residue or ResidueId.BEGINNING_OF_CHAIN if the beginning of the chain is found before an equivalent residue
    * @return
    */
   public ResidueId getPreviousEquivalentResidueId(ResidueNumberScheme rns)
   {
      ResidueId result, aResidue = this;
   //   int count = 0;
      while((result = aResidue.getEquivalentResidueId(rns)) == null)
      {
         aResidue = aResidue.getPrevious();
         this.ensureAfter(aResidue);
         //System.out.println("getPreviousEquivalentResidueId call " + ++count);
      }
      return result;
   }
   
   public boolean hasDbrefMapping()
   {
      return residueNumberScheme == DBREF || getEquivalentResidueId(DBREF) != null;
   }
   
   public boolean hasDbrefMismatch()
   {
      boolean result;
      if(hasDbrefMapping())
      {
         ResidueId uniprotRes;
         if(residueNumberScheme != DBREF)
         {
            uniprotRes = getEquivalentResidueId(DBREF);
            
         }
         else // if we're looking at a uniprot residue, we know there's a mismatch by comparing to mmcif
         {
            uniprotRes = getEquivalentResidueId(SEQRES);
         }
         result = !(uniprotRes == null || uniprotRes.getResidueInfo().equals(residueInfo));
      }
      else
      {
         result = false;
      }
      return result;
   }
   
   public boolean isNonStandard()
   {
      if ( residueInfo == null ) {
         System.err.println("residueInfo == null! this should not be the case...");
         return false;
      }
      else
      {
         return residueInfo.isNonstandard();         
      }
   }
   
   public boolean isAfter(ResidueId aResideId)
   {
      ensureResiduesComparable(this, aResideId);
      return compareTo(aResideId) > 0;
   }
   
   public boolean isBefore(ResidueId aResidueId)
   {
      ensureResiduesComparable(this, aResidueId);
      return compareTo(aResidueId) < 0;
   }
   
   public boolean isBetween(ResidueId lowerBound, ResidueId upperBound)
   {
      ensureResiduesComparable(lowerBound, upperBound);
      ensureResiduesComparable(this, lowerBound);
      return isAfter(lowerBound) && isBefore(upperBound);
   }
   
   public boolean isOnSameChainAs(ResidueId aResidueId)
   {
      return areResiduesComparable(this, aResidueId);
   }
   
   public boolean isOnChain(Chain c)
   {
      return chain == c;
   }
   
   public void ensureOnSameChainAs(ResidueId aResidueId)
   {
      ensureResiduesComparable(this, aResidueId);
   }
   
   public void ensureAfter(ResidueId aResidueId)
   {
      if(!isAfter(aResidueId)) throw new RuntimeException(this + " should be after " + aResidueId);
   }
   
   public void ensureBefore(ResidueId aResidueId)
   {
      if(!isBefore(aResidueId)) throw new RuntimeException(this + " should be before " + aResidueId);
   }
   
   public void ensureAfterOrEqual(ResidueId aResidueId)
   {
      if(!(equals(aResidueId) || isAfter(aResidueId))) throw new RuntimeException(this + " should be equal to or after " + aResidueId);
   }
   
   public void ensureBeforeOrEqual(ResidueId aResidueId)
   {
      if(!(equals(aResidueId) || isBefore(aResidueId))) throw new RuntimeException(this + " should be equal to or before " + aResidueId);
   }
   
   public void ensureBetween(ResidueId lowerBound, ResidueId upperBound)
   {
      if(!isBetween(lowerBound, upperBound)) throw new RuntimeException(this + " should be between " + lowerBound + " and " + upperBound);
   }
   
   public void ensureOnChain(Chain c)
   {
      if(!isOnChain(c)) throw new RuntimeException(this + " should be on chain " + c);
   }
   
   /**
     * Constructs a <code>String</code> representation of this residueId
     *
     * @return a <code>String</code> representation 
     * of this object.
     */
    @Override
   public String toString()
    {
        if(this == BEGINNING_OF_CHAIN)
        {
           return("Beginning of chain marker residue");
        }
        if(this == END_OF_CHAIN)
        {
           return("End of chain marker residue");
        }
       
        StringBuilder retValue = new StringBuilder();
           
        retValue.append(this.residueNumberScheme.getShortDescription())
                .append(':')
                .append(this.chain.getStructureId())
                .append(':')
                .append(this.chain.getChainId());
        if(!this.chain.getChainId().equals(this.chain.getPdbChainId().toString()))
        {
           retValue.append('(')
                   .append(this.chain.getPdbChainId())
                   .append(')');
        }
        retValue.append(':')
                .append(this.seqId);
        
        if(this.insertionCode != null)
        {
           retValue.append(this.insertionCode);
        }
        
        retValue.append('(')
                .append(this.residueInfo.getMonId())
                .append(')');
        
        
        retValue.append(equivalentResidues);
        return retValue.toString();
    }

   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((chain == null || chain.getChainId() == null) ? 0 : chain.getChainId().hashCode());
      result = PRIME * result + ((insertionCode == null) ? 0 : insertionCode.hashCode());
      result = PRIME * result + ((residueInfo == null) ? 0 : residueInfo.hashCode());
      result = PRIME * result + ((residueNumberScheme == null) ? 0 : residueNumberScheme.hashCode());
      result = PRIME * result + ((seqId == null) ? 0 : seqId.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if ( (this == BEGINNING_OF_CHAIN && obj == END_OF_CHAIN ) ||
           (this == END_OF_CHAIN && obj == BEGINNING_OF_CHAIN ) )
         return false;
      if (getClass() != obj.getClass())
         return false;
      final ResidueIdImpl other = (ResidueIdImpl) obj;
      if (seqId == null) {
         if (other.seqId != null)
            return false;
      } else if (!seqId.equals(other.seqId))
         return false;
      if (residueInfo == null) {
         if (other.residueInfo != null)
            return false;
      } else if (!residueInfo.equals(other.residueInfo))
         return false;
      if (insertionCode == null) {
         if (other.insertionCode != null)
            return false;
      } else if (!insertionCode.equals(other.insertionCode))
         return false;
      if (residueNumberScheme == null) {
         if (other.residueNumberScheme != null)
            return false;
      } else if (!residueNumberScheme.equals(other.residueNumberScheme))
         return false;
      if (chain == null) {
         if (other.chain != null)
            return false;
      } else if (!chain.getChainId().equals(other.chain.getChainId()))
         return false;
      return true;
   }

   public int compareTo(ResidueId aThat) 
   {
      final int BEFORE = -1;
      final int EQUAL = 0;
      final int AFTER = 1;

      if ( aThat == null ) throw new NullPointerException();
      if ( this == aThat ) return EQUAL;
      if ( this == BEGINNING_OF_CHAIN || aThat == END_OF_CHAIN ) return BEFORE;
      if ( this == END_OF_CHAIN || aThat == BEGINNING_OF_CHAIN ) return AFTER;
      
      // if they're pdb, it's possible that they are correct except that the residue ids are screwy
      // so try to do the comparison using equivalent mmcif residues
      if(residueNumberScheme == ResidueNumberScheme.ATOM ||
    		  aThat.getResidueNumberScheme() == ResidueNumberScheme.ATOM)
      {
         ResidueId cifThis = this.getEquivalentResidueId(ResidueNumberScheme.SEQRES);
         ResidueId cifThat = aThat.getEquivalentResidueId(ResidueNumberScheme.SEQRES);
         if(cifThis != null && cifThat != null)
         {
            return cifThis.compareTo(cifThat);
         }
      }
      
      int comparison = this.chain.compareTo(aThat.getChain());
      if ( comparison != EQUAL ) return comparison;
      
      comparison = this.residueNumberScheme.compareTo(aThat.getResidueNumberScheme());
      if ( comparison != EQUAL ) return comparison;
      
      comparison = this.seqId.compareTo(aThat.getSeqId());
      if ( comparison != EQUAL ) return comparison;
      
      if     (this.insertionCode == null && aThat.getInsertionCode() != null) return BEFORE;
      else if(this.insertionCode != null && aThat.getInsertionCode() == null) return AFTER;
      else if(this.insertionCode != null && aThat.getInsertionCode() != null)
      {
         comparison = this.insertionCode.compareTo(aThat.getInsertionCode());
         if ( comparison != EQUAL ) return comparison;
      }
      assert this.equals(aThat) : "compareTo inconsistent with equals.";

      throw new RuntimeException("Two sufficiently equal but non-identical chains are being compared. this should never happen");
   }
   
   public boolean hasStructuralData()
   {
      return this.getEquivalentResidueId(ATOM) != null;
   }
   
   /*
    * Very specialised Map implementation that only works for ResidueNumberSchemes:ResidueId key value pairs.
    * 
    * Implemented for speed -- no hashes or collections required
    */
   private class EquivalentResidues implements Map<ResidueNumberScheme, ResidueId>, Serializable
   {
      private static final long serialVersionUID = 1L;

      private ResidueId pdbResId   = null;
      private ResidueId mmcifResId = null;
      private ResidueId dbrefResId = null;
      private ResidueId arrayResId = null;
      
      EquivalentResidues(boolean iAmAConstant)
      {
         super();
         ResidueId parent = ResidueIdImpl.this;
         pdbResId   = parent;
         mmcifResId = parent;
         dbrefResId = parent;
         arrayResId = parent;
      }
      
      EquivalentResidues() {
         super();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(Object key) {
         return get(key) != null;
      }

      public boolean containsValue(Object value) {
         if(value instanceof ResidueId)
         {
            return value == get(((ResidueId)value).getResidueNumberScheme());
         }
         
         return false;
      }

      public Set<java.util.Map.Entry<ResidueNumberScheme, ResidueId>> entrySet() {
         throw new UnsupportedOperationException();
      }

      public ResidueId put(ResidueNumberScheme key, ResidueId value) {
         return getOrPut( key, value, true);
      }

      public ResidueId get(Object key) {
         if(!(key instanceof ResidueNumberScheme)) return null;
         return getOrPut( (ResidueNumberScheme)key, null, false);
      }
      
      private ResidueId getOrPut(ResidueNumberScheme key, ResidueId value, boolean amPutting)
      {
    	  
    	  ResidueId result = null;
         switch(key)
         {
            case ATOM:
               result = pdbResId;
               if(amPutting) pdbResId = value;
               break;
            case SEQRES:
               result = mmcifResId;
               if(amPutting) mmcifResId = value;
               break;
            case DBREF:
               result = dbrefResId;
               if(amPutting) dbrefResId = value;
               break;
            case _ARRAY_IDX:
               result = arrayResId;
               if(amPutting)arrayResId = value;
               break;
            default:
               throw new RuntimeException("Don't recognise ResidueNumberScheme " + key);
         }
         return result;
      }

      public boolean isEmpty() {
         return false; // should always be one
      }

      public Set<ResidueNumberScheme> keySet() {
         throw new UnsupportedOperationException("Use ResidueNumberScheme.values() instead");
      }

      public void putAll(Map<? extends ResidueNumberScheme, ? extends ResidueId> t) {
         throw new UnsupportedOperationException();
      }

      public ResidueId remove(Object key) {
         ResidueId result = get(key);
         put((ResidueNumberScheme) key, null);
         return result;
      }

      public int size() {
         int result = 0;
         
         if(pdbResId != null) ++result;
         if(mmcifResId != null) ++result;
         if(dbrefResId != null) ++result;
         if(arrayResId != null) ++result;
         
         return result;
      }

      public Collection<ResidueId> values() {
         Collection<ResidueId> result = new ArrayList<ResidueId>();
         ResidueId rId;
         for( ResidueNumberScheme rns : ResidueNumberScheme.values() )
         {
            rId = get(rns);
            if(rId != null) result.add(rId);
         }
         return result;
      }
      
      public String toString(){
    	  return "EQR:" + size() ;
    	  //+ " pdbCoord:" + pdbResId.getResidueInfo() + " SEQRES:" + mmcifResId.getResidueInfo();
      }
      
   }

   public boolean isBeginningOfChainMarker() {
      return this == BEGINNING_OF_CHAIN;
   }

   public boolean isEndOfChainMarker() {
      return this == END_OF_CHAIN;
   }
}
