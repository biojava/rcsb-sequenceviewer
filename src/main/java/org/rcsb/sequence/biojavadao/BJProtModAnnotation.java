package org.rcsb.sequence.biojavadao;

import java.util.HashSet;
import java.util.Set;


import static org.rcsb.sequence.conf.AnnotationClassification.structuralFeature;
import static org.rcsb.sequence.model.ResidueNumberScheme.ATOM;
import static org.rcsb.sequence.model.ResidueNumberScheme.SEQRES;


import org.biojava.bio.structure.Chain;
import org.biojava3.protmod.ProteinModification;
import org.biojava3.protmod.io.ModifiedCompoundXMLConverter;
import org.biojava3.protmod.ProteinModificationRegistry;
import org.biojava3.protmod.structure.ModifiedCompound;
import org.biojava3.protmod.structure.ProteinModificationIdentifier;
import org.biojava3.protmod.structure.StructureGroup;
import org.rcsb.sequence.annotations.ProtModValue;
import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.AbstractAnnotationGroup;
import org.rcsb.sequence.core.ProtModAnnotationGroup;
import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.util.AnnotationConstants;

public class BJProtModAnnotation extends AbstractAnnotationGroup<ModifiedCompound> implements ProtModAnnotationGroup {
	
	private static final long serialVersionUID = -7395869127810790810L;
	
	private BioJavaChainProxy proxy ;
	private Set<ProteinModification> protMods;
	
	public static final String annotationName =  AnnotationConstants.proteinModification; 
	
	public BJProtModAnnotation(BioJavaChainProxy chain,AnnotationClassification ac, 
			AnnotationName name){
		super(ac, name, SEQRES, chain);
		this.proxy = chain;
		this.protMods = null;
	}
	
	public void setProtMods(Set<ProteinModification> protMods) {
		this.protMods = protMods;
	}
	
	public BJProtModAnnotation(Sequence sequence){
	    super(structuralFeature, AnnotationRegistry.getAnnotationByName(annotationName), SEQRES, sequence);
	}
	
	@Override
	protected void constructAnnotationsImpl() throws Exception {
		
		
		// this class can only deal with SSbonds on the same chain...
		// the view can't draw lines across images...

		Chain bj = proxy.getBJChain();
		
	
		final ProteinModificationIdentifier ptmIdentifier = new ProteinModificationIdentifier();
		
		ptmIdentifier.setRecordAdditionalAttachments(false);
						
		ptmIdentifier.identify(bj, protMods!=null ? protMods : ProteinModificationRegistry.allModifications());
		
		
		Set<ModifiedCompound> modComps = ptmIdentifier.getIdentifiedModifiedCompound();
		
		System.out.println("We identified " + modComps.size() + " modifications on chain " + bj.getChainID());
		
		for (ModifiedCompound mc : modComps) {
			System.out.println("====");
			System.out.println("Modified compound: ");
			System.out.println(mc.toString());
			System.out.println("    " + mc.getModification().toString());
			String xml = ModifiedCompoundXMLConverter.toXML(mc);
			System.out.println(xml);
			if (mc.crossChains())
				continue; // skip cross-chain modifications
			
			ProtModValue cv = new ProtModValue(mc);
			Set<StructureGroup> groups = mc.getGroups(true);
			for (StructureGroup group : groups) {
				ResidueId resId = chain.getResidueId(ATOM, group.getResidueNumber());
				if (!addAnnotation(cv, resId)) {
					System.err.println("Failed to add annotation for "+mc.toString());
				}
			}
		}
	}
	
	@Override
	public boolean annotationsMayOverlap()
	{
		return true;
	}
	
	public Set<ModifiedCompound> getModCompounds() {
		Set<ModifiedCompound> ptms = new HashSet<ModifiedCompound>();
		for (Annotation<ModifiedCompound> mca : getAnnotations()) {
			ModifiedCompound mc = mca.getAnnotationValue().value();
			ptms.add(mc);
		}
		return ptms;
	}

}
