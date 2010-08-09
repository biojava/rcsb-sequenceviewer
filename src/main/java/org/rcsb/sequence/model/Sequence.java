package org.rcsb.sequence.model;

import java.util.Collection;
import java.util.Map;

import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.core.DisulfideAnnotationGroup;
import org.rcsb.sequence.ptm.CrosslinkAnnotationGroup;

/**
 * <p>A <tt>Sequence</tt> may be a complete polymer chain in a structure or a contiguous fragment of a chain.
 * Because the residues in such a sequence are assigned different identifiers in different databases and file formats (e.g. residue 1 
 * in the PDB sequence of 1DDT:A is residue 33 in UniProt P00588), a <tt>Sequence</tt> stores a simple sequence alignment of the 
 * different sequences.</p>
 * <p>The available sequence types are specified in {@link ResidueNumberScheme}. They are:</p>
 * <ul>
 * <li>the sequence specified in SEQRES records</li>
 * <li>the sequence of resolved resiudes specified in ATOM records</li>
 * <li>if available, the sequence from an external sequence database (e.g. UniProt)</li> 
 * </ul>
 * <p>(there is also a zero-based array index representation of the sequence that is not biologically
 * relevant and is used internally by the sequence API)</p>
 * <p>Each residue in each sequence is represented by a {@link ResidueId} object. Individual residue ids may be
 * requested from a sequence individually, as an ordered collection:</p>
 * 
 * <pre>
 * // Get a sequence object
 * Sequence sequence = StructureCollection.get("4HHB").getChain("A");
 * 
 * // Print a string representation of the sequence to stdout
 * PdbLogger.warn( sequence.getSequenceString() );
 * 
 * // Get residue ids from SEQRES record (the default operation)
 * Collection&lt;ResidueId&gt; seqresRecordIds = sequence.getResidueIds();
 * 
 * // Get residue ids from ATOM records
 * Collection&lt;ResidueId&gt; atomRecordIds = sequence.getResidueIds(ResidueNumberScheme.ATOM);
 * </pre>
 * 
 * <p>A sequence also knows what {@link AnnotationGroup}s are available for each sequence. <tt>AnnotationGroup</tt> objects may 
 * be requested using their implementation class:</p>
 * 
 * <pre>
 * // Get a sequence
 * Sequence sequence = StructureCollection.get("4HHB").getChain("A");
 * 
 * // Get SCOP annotation
 * ScopAnnotationGroup scop = sequence.getAnnotationGroup(ScopAnnotationGroup.class);
 * </pre>
 * 
 * @author mulvaney
 * @see ResidueNumberScheme
 * @see ResidueId
 * @see AnnotationGroup
 */
public interface Sequence 
{
   /**
    * Gets the sequence's PDB structure id
    * @return the structure id, e.g. "4HHB"
    */
   public abstract String getStructureId();
   
   /**
    * Gets the sequence's <em>INTERNAL USE ONLY</em> chain id.  Use <tt>getPdbChainId()</tt>
    * for the chain id to show to the public
    * @return the <em>INTERNAL USE ONLY</em> chain id.
    * @see Sequence#getPdbChainId()
    */
   public abstract String getChainId();
   
   /**
    * Gets the sequence's PDB chain id.
    * @return the chain id
    */
   public abstract Character getPdbChainId();
   
   /**
    * Gets the parent <tt>SequenceCollection</tt>.
    * @return the <tt>SequenceCollection</tt>
    * @deprecated PDBWW-1753: this is introducing cycling references
    */
   public abstract SequenceCollection getSequenceCollection();
   
   
   /** Because of the cyclic references every sequence needs to provide a destroy method that allows to clean it up properly
    * PDBWW-1753
    */
   public abstract void destroy();
   
   
   /**
    * Does this chain have residues indexed by a particular residue number scheme? For example,
    * to find out if a dbRef sequence is present:
    * <pre>
    *    Sequence sequence = SequenceCollections.get("4HHB").getChain("A");
    *    if(sequence.hasResiduesIndexedBy(ResidueNumberScheme.DBREF))
    *    {
    *       PdbLogger.warn("We have a dbref sequence!");
    *    }
    * </pre>
    * @param residueNumberScheme
    * @return true if the sequence is annotated by the given residue number scheme
    * @see ResidueNumberScheme
    */
   public abstract boolean hasResiduesIndexedBy(ResidueNumberScheme residueNumberScheme);
   
   /**
    * Finds out what residue number schemes are available for the sequence.
    * @return a <tt>Collection&lt;ResidueNumberScheme&gt;</tt> containing the available residue numebr schemes
    */
   public abstract Collection<ResidueNumberScheme> getAvailableResidueNumberSchemes();
   
   /**
    * Gets the default residue number scheme.  In most cases this will return <tt>ResidueNumberScheme.SEQRES</tt>, but
    * some annotations derived from third parties (e.g. SCOP) will default to <tt>ResidueNumberScheme.PDB</tt> because 
    * those are the residue identifiers used natively. 
    * @return the default <tt>ResidueNumberScheme</tt>
    * @see ResidueNumberScheme
    */
   public abstract ResidueNumberScheme getDefaultResidueNumberScheme();
   
   /**
    * Gets the length of the default sequence (as specified by {@link #getDefaultResidueNumberScheme()})
    * @return sequence length
    */
   public abstract int getSequenceLength();
   
   /**
    * Gets the length of the specified residue number scheme
    * @param rns
    * @return sequence length
    * @see ResidueNumberScheme
    */
   public abstract int getSequenceLength(ResidueNumberScheme rns);
   
   /**
    * Gets a one-character-per-residue string representation of the default sequence (as specified by {@link #getDefaultResidueNumberScheme()}).
    * If no appropriate single character is specified for a given residue, an 'X' will be used.
    * @return the sequence
    * @see ResidueInfo
    */
   public abstract String getSequenceString();
   
   /**
    * Gets a one-character-per-residue string representation of the specified sequence.
    * If no appropriate single character is specified for a given residue, an 'X' will be used.
    * @param rns
    * @return the sequence
    * @see ResidueInfo
    * @see ResidueNumberScheme
    */
   public abstract String getSequenceString(ResidueNumberScheme rns);
   
   /**
    * Gets the {@link AnnotationGroup} specifed. For example:
    * <pre>
    * // Get SCOP annotation
    * ScopAnnotationGroup scop = sequence.getAnnotationGroup(ScopAnnotationGroup.class);
    * </pre>
    * @param clazz the class of the desired {@link AnnotationGroup}
    * @param <T> the supplied class must inherit from {@link AnnotationGroup}
    * @return
    */
   public abstract <T extends AnnotationGroup<?>> T getAnnotationGroup(Class<T> annotationGroupClazz);
   
   
   /** Disulfids are a special type of annotations and they can be accessed via this method
    * 
    *
    * @return a DisulfidAnnotationGroup
    * @deprecated use #getCrossLinkAnnotationGroup for all crosslinks
    */
   public  DisulfideAnnotationGroup  getDisulfideAnnotationGroup();
   
   /**
    * Crosslinks are a special type of annotations and they can be accessed via this method
    * @return
    */
   public CrosslinkAnnotationGroup getCrosslinkAnnotationGroup();
   
   /**
    * Checks whether a particular {@link AnnotationGroup} annotates this sequence
    * @param <T> the annotation value type
    * @param clazz the class of the desired {@link AnnotationGroup}
    * @return
    */
   public abstract <T extends AnnotationGroup<?>> boolean containsAnnotationGroup(Class<T> annotationGroupClazz);
   
   /**
    * Gets a collection of available {@link AnnotationGroup}s for this sequence
    * @return the collection
    */
   public abstract Collection<AnnotationGroup<?>> getAvailableAnnotationGroups();
   
   /**
    * Gets a collection of available {@link AnnotationGroup}s that have useful annotation information
    * @return
    */
   public abstract Collection<AnnotationGroup<?>> getAnnotationGroupsWithData();
   
   /**
    * Gets a collection of available {@link AnnotationGroup}s that have useful annotation information
    * in the specified annotation classification
    * @param ac the {@link AnnotationClassification} within which to look
    * @return
    */
   public abstract Collection<AnnotationGroup<?>> getAnnotationGroupsWithData(final AnnotationClassification ac);
   
   /**
    * Gets the first annotation group that has useful annotation information within the specified classification
    * @param ac the {@link AnnotationClassification} within which to look
    * @return
    */
   public abstract AnnotationGroup<?> getFirstAnnotationGroupWithData(final AnnotationClassification ac);

   /**
    * Get the residue id for the given {@link ResidueNumberScheme} and {@link ResidueId}
    * @param rns
    * @param id
    * @return the resulting {@link ResidueId}
    */
   public abstract ResidueId getResidueId(ResidueNumberScheme rns, Integer id);

   /**
    * Gets the residue id for the given {@link ResidueNumberScheme}, {@link ResidueId} and insertion code
    * @param rns
    * @param id
    * @param insertionCode
    * @return the resulting {@link ResidueId}
    */
   public abstract ResidueId getResidueId(ResidueNumberScheme rns, Integer id, Character insertionCode);

   /**
    * Gets the residue id for the given {@link ResidueNumberScheme} and residue id
    * @param rns
    * @param id
    * @return the resulting {@link ResidueId}
    */
   public abstract ResidueId getResidueId(ResidueNumberScheme rns, String idAsString);

   /**
    * Gets the first {@link ResidueId} (i.e. the N-terminal residue) in the default {@link ResidueNumberScheme}
    * @return
    */
   public abstract ResidueId getFirstResidue();
   
   /**
    * Gets the first {@link ResidueId} (i.e. the N-terminal residue) in the specified {@link ResidueNumberScheme}
    * @param rns
    * @return
    */
   public abstract ResidueId getFirstResidue(ResidueNumberScheme rns);
   
   /**
    * Gets the last {@link ResidueId} (i.e. the C-terminal residue) in the default {@link ResidueNumberScheme}
    * @return
    */
   public abstract ResidueId getLastResidue();
   
   /**
    * Gets the last {@link ResidueId} (i.e. the C-terminal residue) in the specified {@link ResidueNumberScheme}
    * @param rns
    * @return
    */
   public abstract ResidueId getLastResidue(ResidueNumberScheme rns);
   
   /**
    * Does this sequence contain the specified {@link ResidueId}?
    * @param theResidueId
    * @return true if and only if the specified residue id is in the sequence
    */
   public abstract boolean containsResidue(ResidueId theResidueId);
   
   /**
    * Gets the {@link ResidueId}s from the default {@link ResidueNumberScheme} in the order
    * in which they appear in the sequence.
    * @return a <tt>Collection&lt;ResidueId&gt;</tt> containing all residues in chain order.
    */
   public abstract Collection<ResidueId> getResidueIds();

   /**
    * Gets {@link ResidueId}s from the specified {@link ResidueNumberScheme} in the order
    * in which they appear in the sequence.
    * @param rns
    * @return a <tt>Collection&lt;ResidueId&gt;</tt> containing all residues in chain order.
    */
   public abstract Collection<ResidueId> getResidueIds(ResidueNumberScheme rns);
   
   /**
    * Gets all {@link ResidueId}s between the two specified residue ids.  They must be 
    * supplied in the correct order -- i.e. <tt>start</tt> must be before <tt>end</tt> in this
    * sequence
    * @param start
    * @param end
    * @return a <tt>Collection&lt;ResidueId&gt;</tt> containing all residues between <tt>start</tt> and <tt>end</tt> 
    * in the correct order.
    */
   public abstract Collection<ResidueId> getResidueIdsBetween(ResidueId start, ResidueId end);

   /**
    * Gets all {@link ResidueId}s between the two specified residues.  The ids must be 
    * supplied in the correct order -- i.e. <tt>startId</tt> must be before <tt>endId</tt> in this
    * sequence
    * @param rns
    * @param start
    * @param end
    * @return a <tt>Collection&lt;ResidueId&gt;</tt> containing all residues between <tt>start</tt> and <tt>end</tt> 
    * in the correct order.
    */
   public abstract Collection<ResidueId> getResidueIdsBetween(ResidueNumberScheme rns, Integer startId, Integer endId);
   
   /**
    * Is this sequence mapped to an sequence from an external database such as UniProt?
    * @return true if the mapping was established
    */
   public abstract boolean hasDbRefMapping();
   
   /**
    * Gets all {@link ResidueId}s for the given {@link ResidueInfo}
    * @param r
    * @param rns
    * @return a <tt>Collection&lt;ResidueId&gt;</tt> containing all matching residues
    */
   public abstract Collection<ResidueId> getIdsForResidue(ResidueInfo r, ResidueNumberScheme rns);

   /**
    * Gets the polymer type for this sequence
    * @return the {@link PolymerType}
    */
   public abstract PolymerType getPolymerType();
   
   /**
    * Gets a map with keys that are the residues from the sequence in one ResidueNumberScheme that precede gaps 
    * in that sequence when compared to the sequence of another ResidueNumberScheme. The returned 
    * map may contain the marker BEGINNING_OF_CHAIN if the sequence of 
    * the comparison ResidueNumberScheme starts before that of the ResidueNumberScheme of the sequence. In all cases
    * the value is the size of the gap in residues.
    * @param seqRns the ResidueNumberScheme being considered
    * @param gapsRns the compared ResidueNumberScheme
    * @return
    */
   public abstract Map<ResidueId, Integer> getNonContiguousResidueIds(ResidueNumberScheme seqRns, ResidueNumberScheme gapsRns);
   
   /**
    * Gets a string representation of the external database sequence id code, e.g. "P00588"
    * @return
    */
   public abstract String getExternalDbCode();
   
   /**
    * Gets a string representation of the external database sequence id name, e.g. "UniProt"
    * @return
    */
   public abstract String getExternalDbName();
   
   /**
    * Gets a {@link SegmentedSequence} representation of this sequence
    * @param fragmentLength the length of the segments in the <tt>SegmentedSequence</tt>
    * @param rns the {@link ResidueNumberScheme} to use when segmenting the sequence
    * @return the <tt>SegmentedSequence</tt>
    */
   public abstract SegmentedSequence getSegmentedSequence(int fragmentLength, ResidueNumberScheme rns);
   
   /**
    * Gets the {@link Chain} from which this <tt>Sequence</tt> is derived.  If this <tt>Sequence</tt> is, in fact, a <tt>Chain</tt>,
    * this method will return this <tt>Sequence</tt> cast to a <tt>Chain</tt>
    * @return
    */
   public abstract Chain getChain();
   

   public abstract void ensureAnnotated();
}
