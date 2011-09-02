package org.rcsb.sequence.core;

import org.rcsb.sequence.model.SequenceCollection;

public interface SequenceCollectionFactory {

	public SequenceCollection get(String structureId);
}
