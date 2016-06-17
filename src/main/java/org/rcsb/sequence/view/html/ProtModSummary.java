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

import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.protmod.ProteinModification;
import org.biojava.nbio.protmod.structure.ModifiedCompound;
import org.rcsb.sequence.annotations.ProtModValue;
import org.rcsb.sequence.annotations.SimpleSiteModification;
import org.rcsb.sequence.conf.Annotation2Jmol;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.util.ColorWheelUtil;
import org.rcsb.sequence.view.multiline.ProtModDrawerUtil;


public class ProtModSummary extends AnnotationSummaryCell<ModifiedCompound> {

    public ProtModSummary(AnnotationGroup<ModifiedCompound> ag) {
        super(ag);
    }

    @Override
    protected void renderAnnotation(AnnotationValue<ModifiedCompound> av, HtmlElement el) {

        //System.out.println("renderAnntoation " + av);
        ProtModValue pv = (ProtModValue) av;
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

        if(mod instanceof SimpleSiteModification){
            b.append(mod.getDescription());

        }else {
            if(StringUtils.isNotBlank(mod.getPdbccName())) b.append(mod.getPdbccName());

            if(StringUtils.isNotBlank(mod.getResidName())){
                if(b.length() > 0) b.append(" - ");
                b.append(mod.getResidName());
            }else if(StringUtils.isNotBlank(mod.getPsimodName())) {
                if(b.length() > 0) b.append(" - ");
                b.append(mod.getPsimodName());
            }
        }


        if (StringUtils.isNotBlank(mod.getResidId())) {
            b.append(" <i>RESID</i>:<a target=\"_blank\" href=\"http://pir.georgetown.edu/cgi-bin/resid?id=");
            b.append(mod.getResidId());
            b.append("\">");
            b.append(mod.getResidId());
            b.append("<span title=\"external link\" class=\"iconSet-main icon-external\"></span></a>");
        }
        if (StringUtils.isNotBlank(mod.getPsimodId())) {
            b.append(" <i>PSI-MOD</i>:<a target=\"_blank\" href=\"http://www.ebi.ac.uk/ols/ontologies/mod/terms?iri=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2F");
            b.append(mod.getPsimodId().replace(":", "_"));
            b.append("\">");
            b.append(mod.getPsimodId());
            b.append("<span title=\"external link\" class=\"iconSet-main icon-external\"></span></a>");
        }
        if (StringUtils.isNotBlank(mod.getPdbccId())) {
            b.append(" <i>PDB</i>:<a href=\"http://www.pdb.org/pdb/ligand/ligandsummary.do?hetId=");
            b.append(mod.getPdbccId());
            b.append("\">");
            b.append(mod.getPdbccId());
            b.append("</a>");

        }
        System.out.println("summary: " + b.toString());
        return b.toString();
    }
}
