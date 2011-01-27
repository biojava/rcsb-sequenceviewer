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
 * Created on Aug 9, 2010
 * Author: Jianjiong Gao 
 *
 */

package org.rcsb.sequence.view.html;

import org.biojava3.protmod.ProteinModification;
import org.biojava3.protmod.structure.ModifiedCompound;

import org.rcsb.sequence.annotations.ProtModValue;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;



public class ProtModSummary  extends AnnotationSummaryCell<ModifiedCompound> {

	public ProtModSummary(AnnotationGroup<ModifiedCompound> ag) {
		super(ag);
	}

	@Override
	protected void renderAnnotation(AnnotationValue<ModifiedCompound> av, HtmlElement el) {
		ProtModValue pv = (ProtModValue)av;
		ModifiedCompound mc = pv.value();
		//el.replaceContent("["+mc.getModification().getKeywords()+"]");


		String protModLegend = buildHTMLLegend(mc);


		el.replaceContent(protModLegend);
	}

	private String buildHTMLLegend(ModifiedCompound mc) {
		StringBuilder b = new StringBuilder();
		//b.append(mc.toString());
		

		ProteinModification mod = mc.getModification();
		b.append(mod.toString());
		
		if ( mod.getResidId() != null ){
			b.append(" RESID:<a href=\"http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?&#45;newId&#43;[RESID:'");
			b.append(mod.getResidId());
			b.append("']&#43;&#45;view&#43;ResidEntry&#43;&#45;page&#43;qResult\">");
			b.append(mod.getResidId());
			b.append("</a>");
		}
		if ( mod.getPsimodId() != null){
			b.append(" PSI-MOD:<a href=\"http://www.ebi.ac.uk/ontology-lookup/?termId=");
			b.append(mod.getPsimodId());
			b.append("\">");
			b.append(mod.getPsimodId());
			b.append("</a>");		
		}
		if ( mod.getPdbccId() != null){
			b.append(" PDB:<a href=\"http://www.pdb.org/pdb/ligand/ligandsummary.do?hetId=");
			b.append(mod.getPdbccId());
			b.append("\">");
			b.append(mod.getPdbccId());
			b.append("</a>");
		
		}
		return b.toString();
	}
}
