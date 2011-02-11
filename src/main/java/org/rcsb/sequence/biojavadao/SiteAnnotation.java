package org.rcsb.sequence.biojavadao;

import static org.rcsb.sequence.conf.AnnotationClassification.structuralFeature;
import static org.rcsb.sequence.model.ResidueNumberScheme.ATOM;
import static org.rcsb.sequence.model.ResidueNumberScheme.SEQRES;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.biojava.bio.structure.AminoAcid;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Site;
import org.biojava3.protmod.Component;
import org.biojava3.protmod.ComponentType;
import org.biojava3.protmod.ModificationCategory;
import org.biojava3.protmod.ModificationCondition;
import org.biojava3.protmod.ModificationConditionImpl;
import org.biojava3.protmod.ModificationLinkage;
import org.biojava3.protmod.ModificationOccurrenceType;
import org.biojava3.protmod.ProteinModification;
import org.biojava3.protmod.ProteinModificationRegistry;
import org.biojava3.protmod.structure.ModifiedCompound;
import org.biojava3.protmod.structure.ModifiedCompoundImpl;

import org.biojava3.protmod.structure.StructureGroup;
import org.rcsb.sequence.annotations.ProtModValue;
import org.rcsb.sequence.annotations.SimpleSiteModification;
import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.AbstractAnnotationGroup;
import org.rcsb.sequence.core.ProtModAnnotationGroup;
import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.util.AnnotationConstants;

public class SiteAnnotation extends AbstractAnnotationGroup<ModifiedCompound> implements ProtModAnnotationGroup{

	private static final long serialVersionUID = -7395869127810790810L;

	private BioJavaChainProxy proxy ;

	public static final String annotationName = AnnotationConstants.siteRecord; 

	public SiteAnnotation(BioJavaChainProxy chain,AnnotationClassification ac, 
			AnnotationName name){
		super(ac, name, SEQRES, chain);
		this.proxy = chain;
	

	}

	public SiteAnnotation(Sequence sequence){
		super(structuralFeature, AnnotationRegistry.getAnnotationByName(annotationName), SEQRES, sequence);
	}

	@Override
	protected void constructAnnotationsImpl() throws Exception {


		// this class can only deal with SSbonds on the same chain...
		// the view can't draw lines across images...

		Chain bj = proxy.getBJChain();

		
		List<Site> sites = bj.getParent().getSites();
				
		for ( Site s: sites){
			List<Group> groups = s.getGroups();

			boolean matchingSite = false;
			for ( Group g: groups){

				if (g.getChain().getChainID().equals(proxy.getChainId())) {
					matchingSite = true;
					// SITE on correct chain...
					break;
				}
			}

			if ( matchingSite){
				//System.out.println("found a site: " + s);


				Set<StructureGroup> sgroups = new TreeSet<StructureGroup>();
				for ( Group g : groups){
					if ( g instanceof AminoAcid) {
						StructureGroup sg = new StructureGroup();
						sg.setChainId(g.getChainId());
						sg.setPDBResidueNumber(g.getResidueNumber());
						sg.setInsCode(g.getResidueNumber().getInsCode());
						sg.setResidueNumber(g.getResidueNumber().getSeqNum());
						sg.setPDBName(g.getPDBName());
						sg.setType(ComponentType.AMINOACID);
						sgroups.add(sg);
					}


				}
				

				ModifiedCompound mc = new ModifiedCompoundImpl();
				
				ProteinModification modi = getProteinModification(bj.getParent().getPDBCode(), bj.getChainID(), s, groups);
				
				mc.setDescription("SITE " + s.getDescription());
				mc.setGroups(sgroups);
				//System.out.println("modification:" + modi);
				mc.setModification(modi);
				
		
				
				ProtModValue cv = new ProtModValue(mc);
				

				for (StructureGroup group : sgroups) {
					if (group.isAminoAcid()) {
						ResidueId resId = chain.getResidueId(ATOM, group.getResidueNumber());
						addAnnotation(cv, resId);
						//System.out.println("Adding annotation : " + cv + " " + resId);
					}
				}

			}

		}






	}

	private ProteinModification getProteinModification(String pdbId, String chainId, Site s,
			List<Group> groups) {


		String key = chain.getStructureId() + "_" + chain.getPdbChainId()+"_" + s.getSiteID()+"_"+groups.size();

		ProteinModification p = ProteinModificationRegistry.getById(key);
		if ( p != null)
			return p;

		SimpleSiteModification pm = new SimpleSiteModification();
		pm.setId(key);

		ModificationCategory category = ModificationCategory.CROSS_LINK_1;
//		if ( groups.size() > 0 && groups.size()< 8) {
//			String label = "crosslink" + groups.size();
//			category = ModificationCategory.getByLabel(label);
//		} else if ( groups.size() > 7)
//			category = ModificationCategory.CROSS_LINK_8_OR_LARGE;
		pm.setCategory(category);
		pm.setOccurrence(ModificationOccurrenceType.HYPOTHETICAL);

		List<Component> components = new ArrayList<Component>(2);
		components.add(Component.of("COMP1", ComponentType.AMINOACID));
		components.add(Component.of("COMP2", ComponentType.AMINOACID, true, false));

		ModificationLinkage linkage = new ModificationLinkage(components, 0, "ATOM1", 1, "ATOM2");

		ModificationCondition condition = new ModificationConditionImpl(components, Collections.singletonList(linkage));
		pm.setCondition(condition);		
		pm.setKeywords(Collections.EMPTY_SET);
		pm.setDescription(s.getDescription() + " (" + s.getEvCode() + ")");
		try {
			//ProteinModificationRegistry.register(pm);
		} catch (Exception e){
			e.printStackTrace();
		}
		return pm;


	}

	
	
	@Override
	public boolean annotationsMayOverlap()
	{
		return false;
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
