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

package org.rcsb.sequence.ptm;

import java.util.Set;
import java.util.HashSet;


import org.biojava3.protmod.ModificationCategory;
import org.biojava3.protmod.ProteinModification;

import org.rcsb.sequence.biojavadao.BioJavaChainProxy;
import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.Sequence;

public class CrosslinkAnnotationGroup
extends PTMAnnotationGroup {
	private static final long serialVersionUID = -8139994775645142829L;
	
	public static final String annotationName = "crosslink"; 
	
	public CrosslinkAnnotationGroup(BioJavaChainProxy chain,AnnotationClassification ac, 
			AnnotationName name){
		super(chain, ac, name, crossLinkMods);
	}

	
	public CrosslinkAnnotationGroup(Sequence sequence){
	    super(sequence, annotationName);
	}
	
	final static Set<ProteinModification> crossLinkMods = new HashSet<ProteinModification>();
	static {
		for (ModificationCategory cat : ModificationCategory.values()) {
			if (cat.isCrossLink()) {
				crossLinkMods.addAll(ProteinModification.getByCategory(cat));
			}
		}
	}
}
