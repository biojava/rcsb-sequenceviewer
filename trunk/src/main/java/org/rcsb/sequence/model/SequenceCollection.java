package org.rcsb.sequence.model;

import java.util.Collection;
import java.util.Map;


/**
 * A <tt>SequenceCollection</tt> contains all polymer {@link Chain}s for a given structure, and
 * has useful methods for finding the <tt>Chain</tt> or <tt>Chain</tt>s  of interest.
 * @author mulvaney
 *
 */
public interface SequenceCollection 
{
   /**
    * Get the structure id
    * @return
    */
   public String getStructureId();
   
   /**
    * Get the structure title
    * @return
    */
   public String getStructureTitle();
   
   
   /** since we have circular references, add a destroy method to clean up the whole structure
    * 
    */
   public void destroy();
   
   /**
    * <p>Get all chains in a <tt>Map&lt;String, {@link Chain}&gt;</tt>. The <tt>String</tt> key is the
    * chain id.</p>
    * 
    * <p><em>NB this is the <b>INTERNAL PDB CHAIN ID</b> that is only present in mmCIF files and the 
    * database. It should <b>NOT</b> be presented to users. Additionally, requesting a chain using the 
    * chain ids specified in a PDB file will give unexpected results for many, many structures.</em></p>
    * 
    * @return
    */
   public Map<String, Chain> getChains();
   
   /**
    * Get all chains for the given entityId
    * @param entityId
    * @return 
    */
   public Collection<Chain> getChains(Integer entityId);
   
   /**
    * <p>Does this sequence collection include a chain with the specified id?</p>
    * 
    * <p><em>NB this is the <b>INTERNAL PDB CHAIN ID</b> that is only present in mmCIF files and the 
    * database. It should <b>NOT</b> be presented to users. Additionally, requesting a chain using the 
    * chain ids specified in a PDB file will give unexpected results for many, many structures.</em></p>
    * 
    * @param chainId
    * @return
    */
   public boolean containsChain(String chainId);
   
   /**
    * <p>Get the {@link Chain} for the given chain id.</p>
    * 
    * <p><em>NB this is the <b>INTERNAL PDB CHAIN ID</b> that is only present in mmCIF files and the 
    * database. It should <b>NOT</b> be presented to users. Additionally, requesting a chain using the 
    * chain ids specified in a PDB file will give unexpected results for many, many structures.</em></p>
    * 
    * @param chainId
    * @return
    */
   public Chain getChain(String chainId);
   
   
   /** returns the Chain based on the public (PDB file) chain ID.
    * 
    * @param chainId
    * @return
    */
   public Chain getChainByPDBID(String chainId);
   
   /**
    * Get a count of all polymer chains for this structure.
    * @return
    */
   public int chainCount();
   
   /**
    * <p>Get the first chain from each entity in a <tt>Map&lt;String, {@link Chain}&gt;</tt>. The <tt>String</tt> key is the
    * chain id.</p>
    * 
    * <p><em>NB this is the <b>INTERNAL PDB CHAIN ID</b> that is only present in mmCIF files and the 
    * database. It should <b>NOT</b> be presented to users. Additionally, requesting a chain using the 
    * chain ids specified in a PDB file will give unexpected results for many, many structures.</em></p>
    * 
    * @return
    */
   public Map<String, Chain> getFirstChainFromEachEntityMap();
   
   /**
    * <p>Get all <tt>Chain</tt>s in a <tt>Map&lt;{@link PolymerType}, {@link Chain}&gt;</tt>
    * @return
    */
   public Map<PolymerType, Collection<Chain>> getPolymerTypeChainMap();
   
   /**
    * Get a {@link SegmentedSequence} representation of the specified sequence.
    * 
    * <p><em>NB the <b>INTERNAL PDB CHAIN ID</b> that is only present in mmCIF files and the 
    * database is used to pick the sequence to segment. This chain id should <b>NOT</b> be presented to users. Additionally, requesting a chain using the 
    * chain ids specified in a PDB file will give unexpected results for many, many structures.</em></p>
    * 
    * 
    * @param rns the {@link ResidueNumberScheme} to use when segmenting the sequence
    * @param chainId
    * @param fragmentLength the length of the segments in the <tt>SegmentedSequence</tt>
    * @return
    */
   public SegmentedSequence getSegmentedSequence(ResidueNumberScheme rns, String chainId, int fragmentLength);
}
