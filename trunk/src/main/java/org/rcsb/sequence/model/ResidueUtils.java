package org.rcsb.sequence.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import org.rcsb.sequence.core.ResidueIdImpl;

/**
 * Static utility class containing useful {@link ResidueId} methods
 * @author mulvaney
 *
 */
public class ResidueUtils {
   /**
    * Get all {@link ResidueId}s between the two specified ones
    * @param start
    * @param end
    * @return
    * @throws RuntimeException if either residue is an end-of-chain marker, or if start and end are the wrong
    * way around
    */
   public static Collection<ResidueId> getResidueIdsBetween(ResidueId start, ResidueId end)
   {
      if(start == null || end == null)
      {
         throw new NullPointerException("ResidueUtils: Can't get residues between two extremes if one of them is null");
      }
      if(start.isBeginningOfChainMarker() || end.isEndOfChainMarker())
      {
         throw new RuntimeException("ResidueUtils: BEGINNING_OF_CHAIN and END_OF_CHAIN are not real residues: " +
               "They are connected to each other by every complete chain. " +
         "This is a problem because I'm presented with a choice and I'm no good with choices");
      }
      ensureResiduesComparable(start, end);
      if(start.compareTo(end) > 0)
      {
         throw new RuntimeException("ResidueUtils: Can't create sequence backwards -- supply start and end the other way around");
      }

      LinkedList<ResidueId> result = new LinkedList<ResidueId>();

      // if it's just one residue...
      if(start.compareTo(end) == 0)
      {
         //         result = new LinkedList<ResidueId>();
         result.add(start);
      }
      // if start is the beginning of chain marker, start from teh end and work backwards
      else if(start.isBeginningOfChainMarker())
      {
         ResidueId rid = end;
         //         result = new LinkedList<ResidueId>();

         do
         {
            result.addFirst(rid);
         }
         while( !(rid = rid.getPrevious()).equals(start) );
      }
      else
      {
         ResidueId rid = start;
         do
         {
            result.add(rid);
         }
         while( (!(rid = rid.getNext()).equals(end)) && ( rid.hasNext()) );

         if(!end.isEndOfChainMarker())  // don't add the last residue if it's a marker
         {
            result.add(end);
         }
      }
      return result;
   }

   /**
    * Derive the ResidueIdMaps used by implementations of {@link Sequence}
    * @param residueIdMaps
    * @param start
    * @param end
    */
   public static void initDerivedResidueIdMap(Map<ResidueNumberScheme, Map<String, ResidueId>> residueIdMaps, ResidueId start, ResidueId end)
   {
      //    for each residue...
      for(ResidueId rid : getResidueIdsBetween(start, end)) // can't use chain.getResidueIdsBetween(start, end) becasuse t
      {
         // add to each map
         for( ResidueId equivRid : rid.getEquivalentResidueIds() )
         {
            residueIdMaps.get(equivRid.getResidueNumberScheme()).put(equivRid.getSeqIdWithInsertionCode(), equivRid);
         }
      }
   }

   /**
    * Get a collection of residue ids which have missing equivalent residue ids in the given residue number scheme
    * @param sequence
    * @param rns
    * @return
    */
   public static Collection<ResidueId> getResidueIdsWithMissingEquivalent(Iterable<ResidueId> sequence, ResidueNumberScheme rns)
   {
      Collection<ResidueId> result = new LinkedHashSet<ResidueId>();
      if(rns == null || sequence == null)
      {
         throw new NullPointerException();
      }

      for(ResidueId rid : sequence)
      {
         if(rid.getEquivalentResidueId(rns) == null)
         {
            result.add(rid);
         }
      }

      return result;
   }

   /**
    * <p>
    * Get the residues (if any) that are inserted between the supplied residueId and the next one in the given rns
    * </p><p>
    * The resulting collection is guaranteed to contain contiguous residues in the specified rns.
    * </p>
    * @param aResidueId
    * @param rns
    * @return
    */
   public static Collection<ResidueId> getResidueIdsInsertedAfter(ResidueId aResidueId, ResidueNumberScheme rns)
   {
      Collection<ResidueId> result;

      ResidueId next      = aResidueId.getNext();
      ResidueId aResEquiv = aResidueId.getEquivalentResidueId(rns);
      ResidueId equivNext = aResEquiv.getNext();

      ResidueNumberScheme aRns = aResidueId.getResidueNumberScheme();

      if(aResidueId.getResidueNumberScheme() == rns
            || next == equivNext.getEquivalentResidueId(aRns))
      {
         result = Collections.emptyList();
      }
      else
      {
         result = new LinkedList<ResidueId>();
         do
         {
            result.add(equivNext);
         }
         while( !(equivNext = equivNext.getNext()).isEndOfChainMarker()
               && equivNext.getEquivalentResidueId(aRns) != null );

         if(equivNext.getEquivalentResidueId(aRns) != next)
         {
            throw new RuntimeException("ResidueUtils: ResidueIds may be out of order!");
         }
      }
      return result;
   }

   /**
    * Returns true if the two residueIds are the same,
    * or both have the same residueNumberScheme and are on the same chain. An
    * exception is thrown if either is null or if they are equal but not the
    * same object.
    * @param one
    * @param other
    * @return
    */
   public static boolean areResiduesComparable(ResidueId one, ResidueId other)
   {
      if(one == null || other == null) throw new NullPointerException();
      if(one == other) return true;
      if((one.isBeginningOfChainMarker() || one.isEndOfChainMarker()) || (other.isBeginningOfChainMarker() || other.isEndOfChainMarker())) return true;
      if(one.equals(other))  {
    	  System.err.println("ResidueUtils: Found two equal but non-identical residueIds");
    	  return true;
    	  //throw new RuntimeException("ResidueUtils: Found two equal but non-identical residueIds");
      }
      if(one.getResidueNumberScheme() != other.getResidueNumberScheme()) 
    	  return false;
      if(!one.getChain().getChainId().equals(other.getChain().getChainId())) 
    	  return false;
      return true;
   }

   /**
    * @param one
    * @param other
    * @return
    * @throws RuntimeException if a call to {@link #areResiduesComparable(ResidueId, ResidueId)} would return false
    */
   public static void ensureResiduesComparable(ResidueId one, ResidueId other)
   {
      if(!areResiduesComparable(one, other))
      {
         throw new RuntimeException("ResidueUtils: ResidueIds " + one + " and " + other + " are not comparable");
      }
   }

   /**
    * Get a beginning-of-chain marker sentinel {@link ResidueId}
    * @return
    */
   public static ResidueId getBeginningOfChainMarker()
   {
      return ResidueIdImpl.BEGINNING_OF_CHAIN;
   }

   /**
    * Get an end-of-chain marker sentinel {@link ResidueId}
    * @return
    */
   public static ResidueId getEndOfChainMarker()
   {
      return ResidueIdImpl.END_OF_CHAIN;
   }

}
