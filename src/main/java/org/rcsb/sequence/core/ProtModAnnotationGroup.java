/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 * Created on Aug 8, 2010
 * Author: Jianjiong Gao 
 *
 */

package org.rcsb.sequence.core;

import java.util.Set;
import java.util.HashSet;

import org.biojava.bio.structure.Chain;

import org.biojava3.protmod.ProteinModification;
import org.biojava3.protmod.structure.ModifiedCompound;
import org.biojava3.protmod.structure.ProteinModificationIdentifier;
import org.biojava3.protmod.structure.StructureGroup;

import org.rcsb.sequence.annotations.ProtModValue;
import org.rcsb.sequence.biojavadao.BioJavaChainProxy;
import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.Sequence;

import static org.rcsb.sequence.conf.AnnotationClassification.protmod;
import static org.rcsb.sequence.model.ResidueNumberScheme.ATOM;
import static org.rcsb.sequence.model.ResidueNumberScheme.SEQRES;

public class ProtModAnnotationGroup
extends AbstractAnnotationGroup<ModifiedCompound> {
	private static final long serialVersionUID = -7395869127810790810L;
	
	private BioJavaChainProxy proxy ;
	private Set<ProteinModification> protMods;
	
	public static final String annotationName = "modification"; 
	
	public ProtModAnnotationGroup(BioJavaChainProxy chain,AnnotationClassification ac, 
			AnnotationName name){
		super(ac, name, SEQRES, chain);
		this.proxy = chain;
		this.protMods = null;
	}
	
	public void setProtMods(Set<ProteinModification> protMods) {
		this.protMods = protMods;
	}
	
	public ProtModAnnotationGroup(Sequence sequence){
	    super(protmod, AnnotationRegistry.getAnnotationByName(annotationName), SEQRES, sequence);
	}
	
	@Override
	protected void constructAnnotationsImpl() throws Exception {
		
		
		// this class can only deal with SSbonds on the same chain...
		// the view can't draw lines across images...

		Chain bj = proxy.getBJChain();
		
		final ProteinModificationIdentifier ptmIdentifier = new ProteinModificationIdentifier();
		ptmIdentifier.setRecordAdditionalAttachments(false);
		ptmIdentifier.identify(bj, protMods!=null ? protMods : ProteinModification.allModifications());
		Set<ModifiedCompound> modComps = ptmIdentifier.getIdentifiedModifiedCompound();
		for (ModifiedCompound mc : modComps) {
			ProtModValue cv = new ProtModValue(mc);
			Set<StructureGroup> groups = mc.getGroups();
			for (StructureGroup group : groups) {
				if (group.isAminoAcid()) {
					ResidueId resId = chain.getResidueId(ATOM, group.getResidueNumber());
					addAnnotation(cv, resId);
				}
			}
		}
	}
	
	@Override
	public boolean annotationsMayOverlap()
	{
		return true;
	}
	
	public Set<ModifiedCompound> getPTMs() {
		Set<ModifiedCompound> ptms = new HashSet<ModifiedCompound>();
		for (Annotation<ModifiedCompound> mca : getAnnotations()) {
			ModifiedCompound mc = mca.getAnnotationValue().value();
			ptms.add(mc);
		}
		return ptms;
	}
}
