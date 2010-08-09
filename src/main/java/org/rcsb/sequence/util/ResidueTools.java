package org.rcsb.sequence.util;

import static org.rcsb.sequence.model.ResidueNumberScheme.ATOM;
import static org.rcsb.sequence.model.ResidueNumberScheme.SEQRES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.Sequence;

public class ResidueTools {
	  /**
	    * Lookup table to convert standard amino acid's monomer ids to one-letter-codes
	    */
	public static final Map<String, Character> AMINO_ACID_LOOKUP_3TO1;

	   /**
	    * Lookup table to convert standard amino acid's one-letter-codes to monomer ids
	    */
	   public static final Map<Character, String> AMINO_ACID_LOOKUP_1TO3;

	   /**
	    * Lookup table to convert standard nucleic acid's monomer ids to one-letter-codes
	    */
	   public static final Map<String, Character> DNA_LOOKUP_2TO1;

	   /**
	    * Lookup table to convert standard nucleic acid's one-letter-codes to monomer ids
	    */
	   public static final Map<Character, String> DNA_LOOKUP_1TO2;

	   /**
	    * Lookup list of standard ribo nucleic acid's. No need for map since one-letter-code equals the monomer id
	    */
	   public static final List<String> RNA_MON_IDS;   
	   
	   /**
	    * List of all the known standard chem_comp ids
	    */
	   private static final List<String> STD_MON_IDS;

	   /**
	    * Static block that initializes lookup maps and initializes their <tt>ResidueInfo</tt> instances
	    */
	   static
	   {
	      Map<String, Character> foo = new HashMap<String, Character>();
	      foo.put("ALA", 'A');
	      foo.put("ASP", 'D');
	      foo.put("ASN", 'N');
	      foo.put("ASX", 'B');
	      foo.put("ARG", 'R');
	      foo.put("CYS", 'C');
	      foo.put("GLU", 'E');
	      foo.put("GLN", 'Q');
	      foo.put("GLY", 'G');
	      foo.put("GLX", 'Z');
	      foo.put("HIS", 'H');
	      foo.put("ILE", 'I');
	      foo.put("LYS", 'K');
	      foo.put("LEU", 'L');
	      foo.put("MET", 'M');
	      foo.put("PHE", 'F');
	      foo.put("PRO", 'P');
	      foo.put("SER", 'S');
	      foo.put("THR", 'T');
	      foo.put("TRP", 'W');
	      foo.put("TYR", 'Y');
	      foo.put("VAL", 'V');
	      AMINO_ACID_LOOKUP_3TO1 = Collections.unmodifiableMap((Collections.synchronizedMap(foo)));

	      Map<Character, String> bar = new HashMap<Character, String>();
	      bar.put('A', "ALA");
	      bar.put('D', "ASP");
	      bar.put('N', "ASN");
	      bar.put('B', "ASX");
	      bar.put('R', "ARG");
	      bar.put('C', "CYS");
	      bar.put('E', "GLU");
	      bar.put('Q', "GLN");
	      bar.put('G', "GLY");
	      bar.put('Z', "GLX");
	      bar.put('H', "HIS");
	      bar.put('I', "ILE");
	      bar.put('K', "LYS");
	      bar.put('L', "LEU");
	      bar.put('M', "MET");
	      bar.put('F', "PHE");
	      bar.put('P', "PRO");
	      bar.put('S', "SER");
	      bar.put('T', "THR");
	      bar.put('W', "TRP");
	      bar.put('Y', "TYR");
	      bar.put('V', "VAL");
	      AMINO_ACID_LOOKUP_1TO3 = Collections.unmodifiableMap(Collections.synchronizedMap(bar));

	      foo = new HashMap<String, Character>();
	      foo.put("DA",'A');
	      foo.put("DC",'C');
	      foo.put("DG",'G');
	      foo.put("DI",'I');
	      foo.put("DU",'U');
	      foo.put("DT",'T');
	      DNA_LOOKUP_2TO1 = Collections.unmodifiableMap((Collections.synchronizedMap(foo)));

	      bar = new HashMap<Character, String>();
	      bar.put('A',"DA");
	      bar.put('C',"DC");
	      bar.put('G',"DG");
	      bar.put('I',"DI");
	      bar.put('U',"DU");
	      bar.put('T',"DT");
	      DNA_LOOKUP_1TO2 = Collections.unmodifiableMap(Collections.synchronizedMap(bar));

	      RNA_MON_IDS=Arrays.asList(new String[] {
	               "A","C","G","I","T","U"
	      });

	      // initialise standard chemical components
	      List<String> stdIds= new ArrayList<String>(AMINO_ACID_LOOKUP_3TO1.keySet().size()+DNA_LOOKUP_2TO1.keySet().size()+RNA_MON_IDS.size());
	      stdIds.addAll(AMINO_ACID_LOOKUP_3TO1.keySet());
	      stdIds.addAll(DNA_LOOKUP_2TO1.keySet());
	      stdIds.addAll(RNA_MON_IDS);
	      STD_MON_IDS=Collections.unmodifiableList(stdIds);

	   }
	   
	   public static final Character UNKNOWN_ONE_LETTER_CODE = 'X';
	   
	   public static final List<String> getStdMonIds() {
		      return STD_MON_IDS;
		   }
	   
	   public static ResidueId getResidueId(Integer id, Sequence chain)
		{
			ResidueId result = chain.getResidueId(SEQRES, id);
			if(result != null) result = result.getEquivalentResidueId(ATOM);
			if(result == null) System.err.println("Can't find mmcif residue " + id + " on chain " + chain);
			return result;
		}
}
