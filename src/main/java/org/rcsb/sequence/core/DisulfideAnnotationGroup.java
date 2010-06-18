package org.rcsb.sequence.core;

import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.SequenceCollection;

public interface DisulfideAnnotationGroup extends AnnotationGroup<ResidueId>{
	
	/*
	 * the residue might be on a different chain
	 * AP: in that case we now return NULL since the maybeAddAnnotation method only considers same chain disulfid bonds...
	 */
	public ResidueId getResidueId(SequenceCollection sequenceCollection, String chainId, Integer seqId) ;
	
	public void maybeAddAnnotation(ResidueId annotated, ResidueId connected, Float distance);
	
}
