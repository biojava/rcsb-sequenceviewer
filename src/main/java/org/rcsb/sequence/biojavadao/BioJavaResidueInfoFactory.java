package org.rcsb.sequence.biojavadao;

import java.util.Collections;
import java.util.Map;

import org.biojava.bio.structure.io.mmcif.ChemCompGroupFactory;
import org.biojava.bio.structure.io.mmcif.model.ChemComp;
import org.biojava.utils.io.SoftHashMap;
import org.rcsb.sequence.core.ResidueProvider;
import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.ResidueInfo;
import org.rcsb.sequence.model.ResidueInfoFactory;
import org.rcsb.sequence.util.ResidueTools;


public class BioJavaResidueInfoFactory implements ResidueInfoFactory {

	static SoftHashMap cache;
	static {
		 cache = new SoftHashMap(0);
	}
	
	/** Converts biojava chemical components to ResidueInfo classes
	 * 
	 */
	public ResidueInfo getResidue(String monId) {
		
		BioJavaResidueInfo ri = (BioJavaResidueInfo)cache.get(monId);
		if ( ri != null)
			return ri;
		
		ChemComp cc = ChemCompGroupFactory.getChemComp(monId);
		
		ri = new BioJavaResidueInfo();
		ri.setChemComp(cc);
		
		return ri;
	}

	
	public ResidueInfo getResidue(PolymerType pt, Character oneLetterCode) {
			
		 String monId;
	      Map<Character, String> theMapToUse;
	      switch(pt)
	      {
	         case peptide :
	            theMapToUse = ResidueTools.AMINO_ACID_LOOKUP_1TO3;
	            break;
	         case dna :
	            theMapToUse = ResidueTools.DNA_LOOKUP_1TO2;
	            break;
	         case rna :
	            return ResidueProvider.getResidue(oneLetterCode.toString().toUpperCase());
	         default :
	            theMapToUse = Collections.emptyMap();
	         break;
	      }
	      if((monId = theMapToUse.get(oneLetterCode)) != null)
	      {
	         return ResidueProvider.getResidue(monId);
	      }
	      else if((monId = theMapToUse.get(oneLetterCode.toString().toUpperCase().charAt(0))) != null) // TODO find an efficient way to convert Character to upper-case equiv
	      {
	         return ResidueProvider.getResidue(monId);
	      }
	      else
	      {
	         throw new RuntimeException("Could not find " + pt.toString() + " with code " + oneLetterCode);
	      }
	}

}
