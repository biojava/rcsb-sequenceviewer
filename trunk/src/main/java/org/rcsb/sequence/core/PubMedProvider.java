package org.rcsb.sequence.core;

import org.rcsb.sequence.model.PubMed;
import org.rcsb.sequence.model.PubMedFactory;

public class PubMedProvider {

	static PubMedFactory factory;
	
	public static void setPubMedFactory(PubMedFactory fact){
		factory = fact;
	}
	
	public static PubMed getPubMed(Long pubmedID){
		return factory.getPubMed(pubmedID);
	}
}
