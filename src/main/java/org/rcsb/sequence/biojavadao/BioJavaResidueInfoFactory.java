package org.rcsb.sequence.biojavadao;

import org.biojava.bio.structure.io.mmcif.ChemCompGroupFactory;
import org.biojava.bio.structure.io.mmcif.model.ChemComp;
import org.biojava.utils.io.SoftHashMap;
import org.rcsb.sequence.model.ResidueInfo;
import org.rcsb.sequence.model.ResidueInfoFactory;


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

}
