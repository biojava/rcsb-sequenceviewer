package org.rcsb.sequence.model;

import java.util.Collection;




/**
 * <p>A <tt>ResidueId</tt> represents a {@link ResidueInfo} in its context within a {@link Chain} 
 * and {@link ResidueNumberScheme}. It can be thought of as an element in a doubly-linked list: 
 * It knows the <tt>ResidueIds</tt> before and after it in the {@link Chain}. In addition, it also
 * knows its <em>equivalent <tt>ResidueId</tt>s</em> -- those <tt>ResidueId</tt>s that represent
 * the same residue in other {@link ResidueNumberSchemes}.</p>
 * 
 * <p>For example (based on code in {@link TestUniprotWith12E8}):</p>
   <pre>
   // Get a ResidueId
   Chain chainB = SequenceCollections.get("12E8").getChain("B");
   ResidueId seqresB105 = chainB.getResidueId(ResidueNumberScheme.SEQRES, 105);
   
   // Get the next ResidueId
   ResidueId seqresB106 = seqresB105.getNext();
   
   // Duplicate ResidueIds are never created by the same SequenceCollection
   assert seqresB106 == chainB.getResidueId(ResidueNumberScheme.SEQRES, 105);
   
   // Get the equivalent residue from DBREF
   ResidueId dbrefB105 = seqresB106.getEquivalentResidue(ResidueNumberScheme.DBREF);
   </pre>
 * @author mulvaney
 *
 */
public interface ResidueId extends Comparable<ResidueId>
{
   /**
    * Get the {@link ResidueNumberScheme} for this <tt>ResidueId</tt>
    * @return
    */
   public abstract ResidueNumberScheme getResidueNumberScheme();
   
   /**
    * Get this <tt>ResidueId</tt>'s {@link ResidueNumberScheme}
    * @return
    */
   public abstract Chain getChain();
   
   /**
    * Get the sequence id
    * @return
    */
   public abstract Integer getSeqId();
   
   /**
    * Get the sequence id as a string, including its insertion code if present
    * @return
    */
   public abstract String getSeqIdWithInsertionCode();
   
   /**
    * <p>Does this <tt>ResidueId</tt> have an insertion code to accompany its sequence id?</p>
    * <p>This method returns the same as the expression <tt>getEquivalentResidue(ResidueNumberScheme.ATOM) != null</tt>
    * @return
    */
   public abstract boolean hasInsertionCode();
   
   /**
    * Get the insertion code, or <tt>null</tt> if none is present
    * @return
    */
   public abstract Character getInsertionCode();
   
   /**
    * Get the {@link PolymerType}
    * @return
    */
   public abstract PolymerType getPolymerType();
   
   /**
    * Get the {@link ResidueType}
    * @return
    */
   public abstract ResidueType getResidueType();
   
   /**
    * Get the {@link ResidueInfo}
    * @return
    */
   public abstract ResidueInfo getResidueInfo();
   /**
    * Does this <tt>ResidueId</tt> correspond to a residue with structural data?
    * @return
    */
   public boolean hasStructuralData();
   
   /**
    * <p>Does this <tt>ResidueId</tt> have a mapping to an external sequence database?</p>
    * <p>This method returns the same as the expression <tt>getEquivalentResidue(ResidueNumberScheme.DBREF) != null</tt>
    * @return
    */
   public abstract boolean hasDbrefMapping();
   
   /**
    * Does this <tt>ResidueId</tt> have a mapping to an external sequence database whose {@link ResidueInfo} does not
    * match with this one's?
    * @return
    */
   public abstract boolean hasDbrefMismatch();
   
   /**
    * Does this <tt>ResidueId</tt> represent a non-standard {@link ResidueInfo}?
    * @return
    */
   public abstract boolean isNonStandard();
   
   
   /**
    * Get the next <tt>ResidueId</tt> in the {@link Chain}
    * @return the next <tt>ResidueId</tt>, or an end-of-chain marker if this was the last
    * <tt>ResidueId</tt> in the <tt>Chain</tt>
    * @see #hasNext()
    * @see #isEndOfChainMarker()
    */
   public abstract ResidueId getNext();
   
   /**
    * Is there another <tt>ResidueId</tt> before the end of the {@link Chain} is reached?
    * @return <tt>true</tt> if there is
    */
   public abstract boolean hasNext();
   
   /**
    * Get the previous <tt>ResidueId</tt> in the {@link Chain}
    * @return return the previous <tt>ResidueId</tt>, or a beginning-of-chain marker if this was the last
    * <tt>ResidueId</tt> in the <tt>Chain</tt>
    * @see #hasNext()
    * @see #isBeginningOfChainMarker()
    */
   public abstract ResidueId getPrevious();
   
   /**
    * Is there another <tt>ResidueId</tt> before the beginning of the {@link Chain} is reached?
    * @return <tt>true</tt> if there is
    */
   public abstract boolean hasPrevious();
   
   /**
    * Get the <tt>ResidueId</tt> corresponding to the same position on the same {@link Chain} but
    * identified by a different {@link ResidueNumberScheme}
    * @param rns
    * @return
    */
   public abstract ResidueId getEquivalentResidueId(ResidueNumberScheme rns);
   
   /**
    * Get all <tt>ResidueId</tt>s corresponding to the same position on the same {@link Chain} but
    * identified by different {@link ResidueNumberScheme}s
    * @return
    */
   public abstract Collection<ResidueId> getEquivalentResidueIds();
   
   /**
    * Traverse this residue id's sequence in the forward direction (i.e. towards the end) to find
    * the first residue with an equivalent residue id in the specified 
    * ResidueNumberScheme.
    * @param rns 
    * @return the found residue or an end-of-chain marker if the end of the chain is found before an equivalent residue
    */
   public abstract ResidueId getNextEquivalentResidueId(ResidueNumberScheme rns);
   
   /**
    * Traverse this residue id's sequence in the reverse direction (i.e. towards the start) to find
    * the first residue with an equivalent residue id in the specified 
    * ResidueNumberScheme.
    * @param rns
    * @return the found residue or a beginning-of-chain marker if the beginning of the chain is found before an equivalent residue
    */
   public abstract ResidueId getPreviousEquivalentResidueId(ResidueNumberScheme rns);

   /**
    * Is this <tt>ResidueId</tt> both in the same {@link Chain} and after the given <tt>ResidueId</tt> in the sequence?
    * @param aResideId
    * @return
    */
   public abstract boolean isAfter(ResidueId aResideId);

   /**
    * Is this <tt>ResidueId</tt> both in the same {@link Chain} and before the given <tt>ResidueId</tt> in the sequence?
    * @param aResideId
    * @return
    */
   public abstract boolean isBefore(ResidueId aResidueId);

   /**
    * Is this <tt>ResidueId</tt> both in the same {@link Chain} and between the two given <tt>ResidueId</tt>s in the sequence?
    * @param lowerBound
    * @param upperBound
    * @return
    */
   public abstract boolean isBetween(ResidueId lowerBound, ResidueId upperBound);
   
   /**
    * Is this <tt>ResidueId</tt> on the same {@link Chain} as the specified one?
    * @param aResidueId
    * @return
    */
   public abstract boolean isOnSameChainAs(ResidueId aResidueId);
   
   /**
    * Is this <tt>ResidueId</tt> on the specified {@link Chain}?
    * @param c
    * @return
    */
   public abstract boolean isOnChain(Chain c);
   
   /**
    * Ensure that this <tt>ResidueId</tt> on the same {@link Chain} as the given <tt>ResidueId</tt>
    * @param aResidueId
    * @throws RuntimeException if not
    */
   public abstract void ensureOnSameChainAs(ResidueId aResidueId);

   /**
    * Ensure that this <tt>ResidueId</tt> both in the same {@link Chain} and after the given <tt>ResidueId</tt> in the sequence
    * @param aResidueId
    * @throws RuntimeException if not
    */
   public abstract void ensureAfter(ResidueId aResidueId);

   /**
    * Ensure that this <tt>ResidueId</tt> both in the same {@link Chain} and before the given <tt>ResidueId</tt> in the sequence
    * @param aResidueId
    * @throws RuntimeException if not
    */
   public abstract void ensureBefore(ResidueId aResidueId);

   /**
    * Ensure that this <tt>ResidueId</tt> both in the same {@link Chain} and either after the given <tt>ResidueId</tt> in the sequence or is the same <tt>ResidueId</tt>
    * @param aResidueId
    * @throws RuntimeException if not
    */
   public abstract void ensureAfterOrEqual(ResidueId aResidueId);

   /**
    * Ensure that this <tt>ResidueId</tt> both in the same {@link Chain} and either before the given <tt>ResidueId</tt> in the sequence or is the same <tt>ResidueId</tt>
    * @param aResidueId
    * @throws RuntimeException if not
    */
   public abstract void ensureBeforeOrEqual(ResidueId aResidueId);

   /**
    * Ensure that this <tt>ResidueId</tt> both in the same {@link Chain} and between the given <tt>ResidueId</tt>s in the sequence
    * @param lowerBound
    * @param upperBound
    *@throws RuntimeException if not
    */
   public abstract void ensureBetween(ResidueId lowerBound, ResidueId upperBound);

   /**
    * Ensure this <tt>ResidueId</tt> on the specified {@link Chain}
    * @param c
    * @throws RuntimeException if not
    */
   public abstract void ensureOnChain(Chain c);
   
   /**
    * Is this <tt>ResidueId</tt> an end-of-chain marker?
    * @return
    */
   public abstract boolean isEndOfChainMarker();
   
   /**
    * Is this <tt>ResidueId</tt> a beginning-of-chain marker?
    * @return
    */
   public abstract boolean isBeginningOfChainMarker();
 
}
