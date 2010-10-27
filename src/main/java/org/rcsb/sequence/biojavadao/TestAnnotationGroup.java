package org.rcsb.sequence.biojavadao;


import static org.rcsb.sequence.model.ResidueNumberScheme.SEQRES;


import org.rcsb.sequence.annotations.DomainDefinitionValue;
import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.AbstractAnnotationGroup;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.Sequence;


public class TestAnnotationGroup extends AbstractAnnotationGroup<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6167403548712038464L;

	BioJavaChainProxy proxy ;

	public static final String annotationName = "scop";
	public TestAnnotationGroup(BioJavaChainProxy chain,AnnotationClassification ac, AnnotationName name){

		super(ac, name, SEQRES, chain);
		this.proxy = chain;
	}

	public TestAnnotationGroup(Sequence sequence){
		super(AnnotationClassification.strdom, AnnotationRegistry.getAnnotationByName(annotationName), SEQRES, sequence);
	}


	@Override
	protected void constructAnnotationsImpl() throws Exception {

		int prevE = 1;

		int seqId1;
		int seqId2;
		int steps = 21;
		int length =  proxy.getBJChain().getSeqResSequence().length() ;
		for ( int i = 0 ; i < steps ;  i++){

			if ( i == 0) {

				seqId1 =1; 				
				
			} else {
				seqId1 = prevE;
			}
			
			seqId2 = Math.round((length / (float)steps) * (i+1));
			prevE = seqId2+1;
			
			//System.out.println(i + " " + length + " seqId1 " + seqId1 + " " + seqId2);
			ResidueId rid1 = getResidueId(proxy,  seqId1);
			ResidueId rid2 = getResidueId(proxy,  seqId2);
			
			addAnnotation(new DomainDefinitionValue("DOMAIN " + (i+1), "TEST"), rid1, rid2);
		}

	}
	/*
	 * the residue might be on a different chain
	 * AP: in that case we now return NULL since the maybeAddAnnotation method only considers same chain disulfid bonds...
	 */
	public ResidueId getResidueId(Sequence c,  Integer seqId) {

		//	      if(c.getStatus() == ChainStatus.instantiated) c.ensureResiduesInstantiated();
		return c.getResidueId(SEQRES, seqId);
	}

}
