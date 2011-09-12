package org.rcsb.sequence.biojavadao;

import org.biojava.bio.structure.Structure;

import org.biojava.bio.structure.align.ce.AbstractUserArgumentProcessor;
import org.biojava.bio.structure.align.util.AtomCache;
import org.biojava.bio.structure.io.FileParsingParameters;
import org.rcsb.sequence.core.SequenceCollectionFactory;
import org.rcsb.sequence.model.SequenceCollection;


public class BioJavaSequenceCollectionFactory implements
SequenceCollectionFactory {

	public SequenceCollection get(String structureId) {

		AtomCache cache = new AtomCache();

		FileParsingParameters params = new FileParsingParameters();
		params.setAlignSeqRes(true);
		params.setLoadChemCompInfo(true);
		params.setHeaderOnly(false);
		params.setParseSecStruc(true);
		params.setUpdateRemediatedFiles(true);
		
		
		cache.setFileParsingParams(params);		
		cache.setAutoFetch(true);
		
		try {
			Structure structure = cache.getStructure(structureId);
			System.out.println("structure sites: " + structure.getSites());
			BioJavaSequenceCollection collection = new BioJavaSequenceCollection();
			collection.setStructure(structure);

			return collection;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

}
