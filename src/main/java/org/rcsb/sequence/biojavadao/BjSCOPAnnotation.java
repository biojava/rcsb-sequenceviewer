package org.rcsb.sequence.biojavadao;

import static org.rcsb.sequence.conf.AnnotationClassification.strdom;
import static org.rcsb.sequence.model.ResidueNumberScheme.ATOM;
import static org.rcsb.sequence.model.ResidueNumberScheme.SEQRES;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.biojava.bio.structure.align.ce.AbstractUserArgumentProcessor;
import org.biojava.bio.structure.scop.ScopDomain;
import org.biojava.bio.structure.scop.ScopInstallation;
import org.rcsb.sequence.annotations.DomainDefinitionValue;


import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.AbstractAnnotationGroup;

import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.Sequence;




public class BjSCOPAnnotation extends AbstractAnnotationGroup<String>implements Serializable { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -6349610585974414220L;
	
	public static final String annotationName = "SCOP";
	
	protected Map<String, ScopDomain> sns;
	
	
	public BjSCOPAnnotation(Sequence sequence){
	    super(strdom, AnnotationRegistry.getAnnotationByName(annotationName), ATOM, sequence);
	}
	
	public BjSCOPAnnotation(BioJavaChainProxy chain,AnnotationClassification ac, 
			AnnotationName name){
		super(ac, name, SEQRES, chain);
		
		this.sns = null;
	}
	
	@Override
	protected void constructAnnotationsImpl() throws Exception {
		//System.out.println("constructing new SCO{ annotation");
		try {
			 getScopNodes(chain);
		}
		catch(Exception e)
		{
			System.err.println("Can't get SCOP comments for " + chain.getStructureId());
			e.printStackTrace();
			sns  = Collections.emptyMap();
		}
		
	}


	public void getScopNodes(Sequence chain)
	{
		// we do two queries and then loop through both lists of results because the hbm files haven't
		// mapped the association between scop_protein_domain and scop_node. 

	  
		String cacheLocation = System.getProperty(AbstractUserArgumentProcessor.PDB_DIR);
		
	   ScopInstallation install = new ScopInstallation(cacheLocation);
		
	   List<ScopDomain> domains =install.getDomainsForPDB(chain.getStructureId());
		
		System.out.println("found " + domains.size() + " domains  for " + chain.getStructureId());
		for (ScopDomain d : domains) {
			
			System.out.println(d);
			DomainDefinitionValue def = new DomainDefinitionValue(d.getClassificationId(),"SCOP");
			
			List<String> ranges = d.getRanges();
			
			for ( String r : ranges){
				System.out.println(r);
				String[] coords = r.split(":");
				if  ( coords.length > 1){
					// if length 1, only provided a Chain id...
					String[] pdbRanges = coords[1].split("-");
					if ( pdbRanges.length!= 2)
						continue;
					String pdbresnumStart = pdbRanges[0].trim();
					String pdbresnumEnd   = pdbRanges[1].trim();

					ResidueId resStart = chain.getResidueId(ATOM, pdbresnumStart);
					ResidueId resEnd = chain.getResidueId(ATOM, pdbresnumEnd);
					addAnnotation(def, resStart, resEnd);
				} else {
					// only a chain ID
					ResidueId resStart = chain.getFirstResidue();
					ResidueId resEnd = chain.getLastResidue();
					addAnnotation(def, resStart, resEnd);
				}
			}
			
		}
		

		
	}

	
	
}
