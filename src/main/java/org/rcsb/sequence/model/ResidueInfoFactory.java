package org.rcsb.sequence.model;


public interface  ResidueInfoFactory {

	ResidueInfo getResidue(String monId);
	ResidueInfo getResidue(PolymerType pt, Character oneLettercode);
}
