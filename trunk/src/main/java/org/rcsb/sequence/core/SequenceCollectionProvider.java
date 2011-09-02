package org.rcsb.sequence.core;

import org.rcsb.sequence.model.SequenceCollection;

public class SequenceCollectionProvider {

	private SequenceCollectionProvider(){}


	static SequenceCollectionFactory factory;
	
	public static void setSequenceCollectionFactory(SequenceCollectionFactory fact){
		factory = fact;
	}
	
	public static synchronized SequenceCollection get(String structureId) {
		return factory.get(structureId);
	}
	
	
}
