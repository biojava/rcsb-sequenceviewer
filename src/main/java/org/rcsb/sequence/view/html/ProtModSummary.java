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
import org.rcsb.sequence.conf.Annotation2Jmol;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;

import org.rcsb.sequence.util.ColorWheelUtil;
import org.rcsb.sequence.view.multiline.ProtModDrawerUtil;



public class ProtModSummary  extends AnnotationSummaryCell<ModifiedCompound> {

	public ProtModSummary(AnnotationGroup<ModifiedCompound> ag) {
		super(ag);
	}

	@Override
	protected void renderAnnotation(AnnotationValue<ModifiedCompound> av, HtmlElement el) {
				
		//System.out.println("renderAnntoation " + av);
		ProtModValue pv = (ProtModValue)av;
		ModifiedCompound mc = pv.value();
		//el.replaceContent("["+mc.getModification().getKeywords()+"]");

		
		
		HtmlElement colouredDomId;
		colouredDomId = new HtmlElement("span");
		colouredDomId.addAttribute("style", "tooltip");
		colouredDomId.addAttribute("title", "View in Jmol");
		colouredDomId.addAttribute("style", "background-color: " + ColorWheelUtil.getColorHex(ProtModDrawerUtil.getColor(mc.getModification())));
		colouredDomId.addAttribute("onclick", Annotation2Jmol.getOnclick(ag, av));
		colouredDomId.addAttribute("class", "clickableIfJmol");
		colouredDomId.appendToContent("&nbsp;")
		.appendToContent(mc.getModification().getId())
		.appendToContent("&nbsp;");
		el.addChild(colouredDomId);

		HtmlElement text = new HtmlElement("span");

		String protModLegend = buildHTMLLegend(mc);
		
		text.appendToContent(protModLegend).appendToContent("&nbsp;");
		
		el.addChild(text);
		
		//System.out.println(el.toString());
		//el.replaceContent(protModLegend);
	}

	private String buildHTMLLegend(ModifiedCompound mc) {
		StringBuilder b = new StringBuilder();
		//b.append(mc.toString());




		ProteinModification mod = mc.getModification();

		b.append(mod.toString());

		if ( mod.getResidId() != null ){
			b.append(" <i>RESID</i>:<a target=\"_blank\" href=\"http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?&#45;newId&#43;[RESID:'");
			b.append(mod.getResidId());
			b.append("']&#43;&#45;view&#43;ResidEntry&#43;&#45;page&#43;qResult\">");
			b.append(mod.getResidId());			
			b.append("<span title=\"external link\" class=\"iconSet-main icon-external\"></a>");
		}
		if ( mod.getPsimodId() != null){
			b.append(" <i>PSI-MOD</i>:<a target=\"_blank\" href=\"http://www.ebi.ac.uk/ontology-lookup/?termId=");
			b.append(mod.getPsimodId());
			b.append("\">");
			b.append(mod.getPsimodId());
			b.append("<span title=\"external link\" class=\"iconSet-main icon-external\"></a>");
		}
		if ( mod.getPdbccId() != null){
			b.append(" <i>PDB</i>:<a href=\"http://www.pdb.org/pdb/ligand/ligandsummary.do?hetId=");
			b.append(mod.getPdbccId());
			b.append("\">");
			b.append(mod.getPdbccId());
			b.append("</a>");

		}
		return b.toString();
	}
}
