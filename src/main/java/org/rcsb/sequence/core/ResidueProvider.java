package org.rcsb.sequence.core;

import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.ResidueInfo;
import org.rcsb.sequence.model.ResidueInfoFactory;

public class ResidueProvider {
	

	static ResidueInfoFactory factory;
	
	private ResidueProvider(){
		
	}
	

	public static void setResidueInfoFactory(ResidueInfoFactory f){
		factory = f;
	}

	public static ResidueInfoFactory getResidueInfoFactory(){
		return factory;
	}


	
	public static ResidueInfo getResidue(String monId) {
		if ( factory == null)
			throw new RuntimeException("Did not provide a ResidueInfoFactory!");
		return factory.getResidue(monId);
	}


	public static ResidueInfo  getResidue(PolymerType pt, Character oneLettercode) {
		if ( factory == null)
			throw new RuntimeException("Did not provide a ResidueInfoFactory!");
		return factory.getResidue( pt,  oneLettercode);
	}
}
