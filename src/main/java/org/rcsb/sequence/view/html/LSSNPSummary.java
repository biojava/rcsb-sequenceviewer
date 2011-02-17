

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
 * Created on 
 * Author: Andreas Prlic 
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



public class LSSNPSummary  extends AnnotationSummaryCell<ModifiedCompound> {

	public LSSNPSummary(AnnotationGroup<ModifiedCompound> ag) {
		super(ag);
	}

	@Override
	protected void renderAnnotation(AnnotationValue<ModifiedCompound> av, HtmlElement el) {
				
		

		String txt = "SNP annotation from LS-SNP";
		
		HtmlElement colouredDomId;
		colouredDomId = new HtmlElement("span");
		colouredDomId.addAttribute("style", "tooltip");
		colouredDomId.addAttribute("title", "View in Jmol");		
		colouredDomId.addAttribute("onclick", Annotation2Jmol.getOnclick(ag, av));
		colouredDomId.addAttribute("class", "clickableIfJmol");
		colouredDomId.appendToContent("&nbsp;")
		.appendToContent(txt)
		.appendToContent("&nbsp;");
		el.addChild(colouredDomId);

		//HtmlElement text = new HtmlElement("span");

//		String protModLegend = txt;
//		
//		text.appendToContent(protModLegend).appendToContent("&nbsp;");
//		
//		el.addChild(text);
		
		//System.out.println(el.toString());
		//el.replaceContent(protModLegend);
	}

	}

