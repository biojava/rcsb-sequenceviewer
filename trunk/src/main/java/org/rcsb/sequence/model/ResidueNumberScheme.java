package org.rcsb.sequence.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;



/**
 * Enumerated type representing different sequence numbering schemes. They are used as keys to extract 
 * particular sequences from {@link Sequence} objects. These are:</p>
 * <ul>
 * <li>the sequence specified in SEQRES records (and in _pdbx_poly_seq_scheme.seq_id)</li>
 * <li>the sequence of resolved resiudes specified in ATOM records (and in _pdbx_poly_seq_scheme.auth_seq_num)</li>
 * <li>if available, the sequence from an external sequence database (e.g. UniProt)</li> 
 * </ul>
 * <p>(there is also a zero-based array index representation of the sequence that is not biologically
 * relevant and is used internally by the sequence API)</p>
 * <p>For example:</p>
 * <pre>
 * // Get a sequence object
 * Sequence sequence = StructureCollection.get("4HHB").getChain("A");
 * 
 * // Get residue ids from ATOM records
 * Collection&lt;ResidueId&gt; atomRecordIds = sequence.getResidueIds(ResidueNumberScheme.ATOM);
 * </pre>
 * 
 * @author mulvaney
 * @see Sequence
 * @see ResidueId
 * @see <a href="http://mmcif.rcsb.org/dictionaries/mmcif_pdbx.dic/Categories/pdbx_poly_seq_scheme.html">_pdbx_poly_seq_scheme</a> (in mmCIF dictionary)
 * @see <a href="http://www.wwpdb.org/documentation/format23/sect3.html">SEQRES</a> (legacy PDB format primary structure)
 * @see <a href="http://www.wwpdb.org/documentation/format23/sect9.html">ATOM</a> (legacy PDB format primary structure)
 * @see <a href="http://www.wwpdb.org/documentation/format3.1-20080211.pdf">PDF file of changes in remediated PDB file format</a>
 * @see <a href="http://http://www.wwpdb.org/docs.html">PDB File format documentation home</a>
 */
public enum ResidueNumberScheme implements Serializable{

   /**
    * Represents the residue identifiers present in the ATOM records of PDB files
    * and the _pdbx_poly_seq_scheme table in mmCIF files
    */
	ATOM("ATOM","Sequence from ATOM records",false, false, true, null), 
	
	/**
	 * Represents the residue identifiers implied by the order of monomers in the
	 * SEQRES records of PDB files and made explicit in the _pdbx_poly_seq_scheme 
	 * table in mmCIF files
	 */
   SEQRES("SEQRES","Sequence from SEQRES records",true, true, false, null), 
   
   /**
    * Represents the position of each monomer in the sequence present in external sequence
    * databases such as uniprot
    * @see <a href="http://www.uniprot.org/">UniProt</a>
    */
   DBREF("dbRef","Sequence from associated sequence database",true, true, false, null), 
   
   /**
    * Represents a zero-based array index representation of the sequence that is not biologically
    * relevant and is used internally by the sequence API
    */
   _ARRAY_IDX("arrayIdx","",true, true, false, 0);
//   sequential("sequential","",true, true, false, 1);
   
	ResidueNumberScheme(String shortDescription, String fullDescription, boolean alwaysInteger, boolean alwaysSequential, 
         boolean hasInsertionCodes, Integer startsAt)
	{
		this.alwaysInteger       = alwaysInteger;
		this.alwaysSequential    = alwaysSequential;
		this.hasInsertionCodes   = hasInsertionCodes;
		this.startsAt            = startsAt;
      
      this.shortDescription = shortDescription;
      this.fullDescription  = fullDescription ;
	}
   
   public static final ResidueNumberScheme DEFAULT_RNS = SEQRES;
	
	private final boolean alwaysInteger;
	private final boolean alwaysSequential;
	private final boolean hasInsertionCodes;
	private final Integer startsAt;
   
   private final String shortDescription;
   private final String  fullDescription;
	
   /**
    * Are the sequence ids of this <tt>ResidueNumberScheme</tt> always integer values
    * @return
    */
	public boolean alwaysInteger()
	{
		return alwaysInteger;
	}
	/**
    * Are the sequence ids of this <tt>ResidueNumberScheme</tt> always sequential 
    * values (do they have predictable ordering with no gaps?)
    * @return
    */
   public boolean alwaysSequential()
	{
		return alwaysSequential;
	}
   
   /**
    * Does this <tt>ResidueNumberScheme</tt> allow for insertion codes?
    * @return
    */
	public boolean hasInsertionCodes()
	{
		return hasInsertionCodes;
	}
	
	/**
	 * Does this <tt>ResidueNumberScheme</tt> have a defined start number (e.g. 0 or 1)?
	 * @return
	 * @see #getStartsAt()
	 */
   public boolean hasDefinedStartNumber()
   {
      return startsAt != null;
   }
   
   /**
    * What is the predictable starting value for this <tt>ResidueNumberScheme</tt>
    * @return
    * @see #hasDefinedStartNumber()
    */
	public Integer getStartsAt()
	{
		return startsAt;
	}
	
	/**
	 * Get the full description of this <tt>ResidueNumberScheme</tt>
	 * @return
	 * @see #getShortDescription()
	 */
   public String getFullDescription() {
      return fullDescription;
   }
   
   /**
    * Get the short description of this <tt>ResidueNumberScheme</tt>
    * @return
    * @see #getFullDescription()
    */
   public String getShortDescription() {
      return shortDescription;
   }
   
   public static Map<ResidueNumberScheme, Map<String, ResidueId>> initEmptyResidueIdMap()
   {
      Map<ResidueNumberScheme, Map<String, ResidueId>> result = new LinkedHashMap<ResidueNumberScheme, Map<String, ResidueId>>();
      for( ResidueNumberScheme rns : values() )
      {
         result.put(rns, new LinkedHashMap<String, ResidueId>());
      }
      return result;
   }
   
}
